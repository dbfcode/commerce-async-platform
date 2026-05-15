#!/usr/bin/env bash
# ===========================================================================
#  OrderFlow Commerce — E2E Hot-Reload Test
# ===========================================================================
#  Validates the full hot-reload pipeline in Docker:
#
#    1. API is healthy and responds with the ORIGINAL message.
#    2. A Java source file is modified (PingResponse message changed).
#    3. inotifywait detects the change → mvn compile → DevTools restarts.
#    4. API now responds with the MODIFIED message.
#    5. The source file is reverted to the original.
#    6. inotifywait detects the revert → mvn compile → DevTools restarts.
#    7. API responds with the ORIGINAL message again.
#
#  Usage:
#    ./scripts/test-hot-reload.sh              # uses running containers
#    ./scripts/test-hot-reload.sh --start      # docker compose up first
#    ./scripts/test-hot-reload.sh --full       # up, test, then down
#
#  Exit codes:
#    0 = all assertions passed
#    1 = assertion failed
#    2 = environment error (API unreachable, timeout, etc.)
# ===========================================================================
set -euo pipefail

# ── Configuration ──────────────────────────────────────────────────────────
API_URL="${API_URL:-http://localhost:8080}"
PING_ENDPOINT="${API_URL}/test/ping"

TARGET_FILE="api/src/main/java/com/orderflow/ecommerce/controllers/TestController.java"
ORIGINAL_MSG='versão 1.'
MODIFIED_MSG='versão 2 — hot reload e2e test'

STARTUP_TIMEOUT=120        # max seconds to wait for API to be ready
HOT_RELOAD_TIMEOUT=60      # max seconds to wait for hot-reload cycle
POLL_INTERVAL=2            # seconds between health polls

# ── Colors ─────────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

# ── Helpers ────────────────────────────────────────────────────────────────
info()  { echo -e "${CYAN}[INFO]${NC}  $*"; }
ok()    { echo -e "${GREEN}[PASS]${NC}  $*"; }
fail()  { echo -e "${RED}[FAIL]${NC}  $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
step()  { echo -e "\n${BOLD}── $* ──${NC}"; }
divider() { echo -e "${CYAN}═══════════════════════════════════════════════════════${NC}"; }

TESTS_RUN=0
TESTS_PASSED=0
TESTS_FAILED=0

assert_eq() {
    local label="$1" expected="$2" actual="$3"
    TESTS_RUN=$((TESTS_RUN + 1))
    if [ "$expected" = "$actual" ]; then
        ok "$label"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        fail "$label"
        echo "       expected: \"$expected\""
        echo "       actual:   \"$actual\""
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

assert_contains() {
    local label="$1" needle="$2" haystack="$3"
    TESTS_RUN=$((TESTS_RUN + 1))
    if echo "$haystack" | grep -qF "$needle"; then
        ok "$label"
        TESTS_PASSED=$((TESTS_PASSED + 1))
    else
        fail "$label"
        echo "       expected to contain: \"$needle\""
        echo "       actual:              \"$haystack\""
        TESTS_FAILED=$((TESTS_FAILED + 1))
    fi
}

get_ping_message() {
    curl -sf "$PING_ENDPOINT" 2>/dev/null \
      | python3 -c "import sys,json; print(json.load(sys.stdin).get('message',''))" 2>/dev/null \
      || echo ""
}

wait_for_api() {
    local timeout=$1
    local elapsed=0
    while [ $elapsed -lt $timeout ]; do
        if curl -sf "$PING_ENDPOINT" > /dev/null 2>&1; then
            return 0
        fi
        sleep "$POLL_INTERVAL"
        elapsed=$((elapsed + POLL_INTERVAL))
    done
    return 1
}

wait_for_message() {
    local expected="$1" timeout="$2"
    local elapsed=0
    while [ $elapsed -lt $timeout ]; do
        local msg
        msg=$(get_ping_message)
        if [ "$msg" = "$expected" ]; then
            info "Detected in ${elapsed}s"
            return 0
        fi
        sleep "$POLL_INTERVAL"
        elapsed=$((elapsed + POLL_INTERVAL))
    done
    return 1
}

cleanup() {
    if [ -f "$TARGET_FILE" ]; then
        if grep -qF "$MODIFIED_MSG" "$TARGET_FILE" 2>/dev/null; then
            warn "Reverting source file (cleanup)..."
            sed -i "s|$MODIFIED_MSG|$ORIGINAL_MSG|g" "$TARGET_FILE"
        fi
    fi
}
trap cleanup EXIT

# ── Parse arguments ────────────────────────────────────────────────────────
DO_START=false
DO_STOP=false

for arg in "$@"; do
    case "$arg" in
        --start) DO_START=true ;;
        --full)  DO_START=true; DO_STOP=true ;;
        --help|-h)
            echo "Usage: $0 [--start | --full]"
            echo "  --start   run 'docker compose up --build -d' before testing"
            echo "  --full    start before and stop after testing"
            exit 0
            ;;
        *) warn "Unknown argument: $arg" ;;
    esac
done

# ── Main ───────────────────────────────────────────────────────────────────
divider
echo -e "${BOLD}  OrderFlow Commerce — E2E Hot-Reload Test${NC}"
divider

cd "$(git rev-parse --show-toplevel 2>/dev/null || echo /workspace)"

# Optionally start Docker Compose
if $DO_START; then
    step "Starting Docker Compose"
    docker compose up --build -d 2>&1 | tail -5
fi

# ── Phase 1: Wait for API ──────────────────────────────────────────────────
step "Phase 1 · Waiting for API to be ready"
info "Endpoint: $PING_ENDPOINT (timeout: ${STARTUP_TIMEOUT}s)"

if ! wait_for_api "$STARTUP_TIMEOUT"; then
    fail "API did not become ready within ${STARTUP_TIMEOUT}s"
    exit 2
fi
ok "API is responding"

# ── Phase 2: Verify original state ────────────────────────────────────────
step "Phase 2 · Verify original response"

CURRENT_MSG=$(get_ping_message)
assert_eq "Ping message is original" "$ORIGINAL_MSG" "$CURRENT_MSG"

FULL_RESPONSE=$(curl -sf "$PING_ENDPOINT" 2>/dev/null || echo "{}")
assert_contains "Response has status field" '"status"' "$FULL_RESPONSE"
assert_contains "Response has timestamp field" '"timestamp"' "$FULL_RESPONSE"
assert_contains "Status is ok" '"ok"' "$FULL_RESPONSE"

if [ $TESTS_FAILED -gt 0 ]; then
    fail "Original state verification failed — aborting"
    exit 1
fi

# ── Phase 3: Modify source file ───────────────────────────────────────────
step "Phase 3 · Modify Java source (trigger hot reload)"

if [ ! -f "$TARGET_FILE" ]; then
    fail "Target file not found: $TARGET_FILE"
    exit 2
fi

info "Changing message: \"$ORIGINAL_MSG\" → \"$MODIFIED_MSG\""
sed -i "s|$ORIGINAL_MSG|$MODIFIED_MSG|g" "$TARGET_FILE"

if grep -qF "$MODIFIED_MSG" "$TARGET_FILE"; then
    ok "Source file modified successfully"
else
    fail "Source file modification failed"
    exit 2
fi

# ── Phase 4: Wait for hot reload with modified message ─────────────────────
step "Phase 4 · Waiting for hot reload (timeout: ${HOT_RELOAD_TIMEOUT}s)"
info "Expecting message: \"$MODIFIED_MSG\""

RELOAD_START=$(date +%s)

if wait_for_message "$MODIFIED_MSG" "$HOT_RELOAD_TIMEOUT"; then
    RELOAD_END=$(date +%s)
    RELOAD_TIME=$((RELOAD_END - RELOAD_START))
    assert_eq "Hot reload delivered modified message" "$MODIFIED_MSG" "$(get_ping_message)"
    ok "Hot reload completed in ${RELOAD_TIME}s ⚡"
else
    ACTUAL=$(get_ping_message)
    fail "Hot reload did not deliver modified message within ${HOT_RELOAD_TIMEOUT}s"
    echo "       expected: \"$MODIFIED_MSG\""
    echo "       actual:   \"$ACTUAL\""
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# ── Phase 5: Revert source file ───────────────────────────────────────────
step "Phase 5 · Revert source file (trigger second hot reload)"

info "Changing message: \"$MODIFIED_MSG\" → \"$ORIGINAL_MSG\""
sed -i "s|$MODIFIED_MSG|$ORIGINAL_MSG|g" "$TARGET_FILE"

if grep -qF "$ORIGINAL_MSG" "$TARGET_FILE"; then
    ok "Source file reverted successfully"
else
    fail "Source file revert failed"
    exit 2
fi

# ── Phase 6: Wait for hot reload with original message ─────────────────────
step "Phase 6 · Waiting for second hot reload (timeout: ${HOT_RELOAD_TIMEOUT}s)"
info "Expecting message: \"$ORIGINAL_MSG\""

RELOAD_START=$(date +%s)

if wait_for_message "$ORIGINAL_MSG" "$HOT_RELOAD_TIMEOUT"; then
    RELOAD_END=$(date +%s)
    RELOAD_TIME=$((RELOAD_END - RELOAD_START))
    assert_eq "Hot reload delivered original message" "$ORIGINAL_MSG" "$(get_ping_message)"
    ok "Second hot reload completed in ${RELOAD_TIME}s ⚡"
else
    ACTUAL=$(get_ping_message)
    fail "Second hot reload did not deliver original message within ${HOT_RELOAD_TIMEOUT}s"
    echo "       expected: \"$ORIGINAL_MSG\""
    echo "       actual:   \"$ACTUAL\""
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_FAILED=$((TESTS_FAILED + 1))
fi

# ── Phase 7: Verify API is still healthy ───────────────────────────────────
step "Phase 7 · Post-reload health check"

if curl -sf "$PING_ENDPOINT" > /dev/null 2>&1; then
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_PASSED=$((TESTS_PASSED + 1))
    ok "API is still healthy after two reload cycles"
else
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_FAILED=$((TESTS_FAILED + 1))
    fail "API is not responding after reload cycles"
fi

# Verify full CRUD still works after reloads
CATEGORY_RESPONSE=$(curl -sf -X POST "$API_URL/categories" \
    -H "Content-Type: application/json" \
    -d '{"name": "HotReloadTest"}' 2>/dev/null || echo "")

if echo "$CATEGORY_RESPONSE" | grep -qF "HotReloadTest"; then
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_PASSED=$((TESTS_PASSED + 1))
    ok "CRUD operations work after hot reload (POST /categories)"

    CATEGORY_ID=$(echo "$CATEGORY_RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin).get('id',''))" 2>/dev/null || echo "")
    if [ -n "$CATEGORY_ID" ]; then
        curl -sf -X DELETE "$API_URL/categories/$CATEGORY_ID" > /dev/null 2>&1 || true
    fi
else
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_FAILED=$((TESTS_FAILED + 1))
    fail "CRUD operations broken after hot reload"
fi

# Verify RabbitMQ integration still works
RABBIT_RESPONSE=$(curl -sf "$API_URL/test/publish-sample-order" 2>/dev/null || echo "")
if echo "$RABBIT_RESPONSE" | grep -qF '"published":true'; then
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_PASSED=$((TESTS_PASSED + 1))
    ok "RabbitMQ event publishing works after hot reload"
else
    TESTS_RUN=$((TESTS_RUN + 1))
    TESTS_FAILED=$((TESTS_FAILED + 1))
    fail "RabbitMQ event publishing broken after hot reload"
fi

# ── Optionally stop Docker Compose ─────────────────────────────────────────
if $DO_STOP; then
    step "Stopping Docker Compose"
    docker compose down 2>&1 | tail -3
fi

# ── Results ────────────────────────────────────────────────────────────────
divider
echo -e "${BOLD}  Results${NC}"
divider
echo -e "  Tests run:    ${BOLD}$TESTS_RUN${NC}"
echo -e "  Passed:       ${GREEN}${BOLD}$TESTS_PASSED${NC}"
if [ $TESTS_FAILED -gt 0 ]; then
    echo -e "  Failed:       ${RED}${BOLD}$TESTS_FAILED${NC}"
fi
divider

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}${BOLD}✅ All hot-reload E2E tests passed!${NC}\n"
    exit 0
else
    echo -e "\n${RED}${BOLD}❌ $TESTS_FAILED test(s) failed.${NC}\n"
    exit 1
fi
