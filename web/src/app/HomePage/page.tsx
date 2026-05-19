import { useState } from "react";
import ProductCard from "../../components/ProductCard";
import ProductCardSkeleton from "../../components/ProductCardSkeleton";
import useProducts from "../../hooks/api/useProducts";

export default function Home() {
  const { products, loading, error } = useProducts();
  const [search, setSearch] = useState("");

  const filtered = products.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <div className="p-4">
      {loading ? (
        <div>
          {/* Busca */}
          <div className="w-full h-11 bg-gray-200 rounded-xl mb-4 animate-pulse"></div>

          {/* Título */}
          <div className="h-6 w-28 bg-gray-200 rounded mb-2 animate-pulse"></div>

          {/* Quantidade */}
          <div className="h-4 w-16 bg-gray-200 rounded mb-4 animate-pulse"></div>

          {/* Cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
            {Array.from({ length: 4 }).map((_, index) => (
              <ProductCardSkeleton key={index} />
            ))}
          </div>
        </div>
      ) : (
        <>
          {!error && (
            <>
              {/* Busca */}
              <input
                type="text"
                placeholder="Buscar produtos..."
                className="w-full bg-surface border border-border rounded-xl px-3 py-2 mb-4"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />

              {/* Título */}
              <h2 className="text-lg font-semibold">Produtos</h2>

              {/* Quantidade */}
              <p className="text-sm text-gray-500 mb-3">
                {filtered.length} itens
              </p>
            </>
          )}

          {/* ERRO */}
          {error ? (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold">Produtos</h2>
              <p className="text-red-500">{error}</p>
              <div className="grid grid-cols-2 gap-3">
                {Array.from({ length: 4 }).map((_, index) => (
                  <ProductCardSkeleton key={index} />
                ))}
              </div>
            </div>
          ) : (
            <>
              {/* Nenhum produto */}
              {filtered.length === 0 && <p>Nenhum produto encontrado.</p>}

              {/* LISTA */}
              {filtered.length > 0 && (
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
                  {filtered.map((product) => (
                    <ProductCard key={product.id} product={product} />
                  ))}
                </div>
              )}
            </>
          )}
        </>
      )}
    </div>
  );
}