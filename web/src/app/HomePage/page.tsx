  import { useEffect, useState } from "react";
  import ProductCard from "../../components/ProductCard";
  import ProductCardSkeleton from "../../components/ProductCardSkeleton";

  type Category = {
    id: number;
    name: string;
  };

  type Product = {
    id: number;
    name: string;
    category?: Category;
    price: number;
  };

  export default function Home() {
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [search, setSearch] = useState("");

    useEffect(() => {
      async function fetchProducts() {
        try {
          setLoading(true);

          await new Promise((resolve) => setTimeout(resolve, 3000));

          const res = await fetch("http://localhost:8080/products");

          if (!res.ok) throw new Error("Erro ao buscar produtos");

          const data: Product[] = await res.json();
          setProducts(data);
        } catch (err: unknown) {
          if (err instanceof Error) {
            setError(err.message);
          } else {
            setError("Erro desconhecido");
          }
        } finally {
          setLoading(false);
        }
      }

      fetchProducts();
    }, []);

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

        {/* ERRO */}
        {error && (
          <p className="text-red-500">{error}</p>
        )}

        {/* Nenhum produto */}
        {filtered.length === 0 && !error && (
          <p>Nenhum produto encontrado.</p>
        )}

        {/* LISTA */}
        {!error && filtered.length > 0 && (
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
            {filtered.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
              />
            ))}
          </div>
        )}
      </>
    )}
  </div>
    );
  }