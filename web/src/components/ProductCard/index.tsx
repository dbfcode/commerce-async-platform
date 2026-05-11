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

  type Props = {
    product: Product; 
  };

  export default function ProductCard({ product }: Props) {
    return (
      <div className="bg-surface rounded-lg p-3 shadow-sm border border-border">
        {/* Imagem */}
        <img
          src={`/products/${product.id}.webp`}
          alt={product.name}
          className="h-20 w-full object-contain rounded mb-2"
        />

        {/* Categoria */}
        {product.category && (
          <span className="text-xs bg-blue-100 text-blue-600 px-2 py-1 rounded">
            {product.category.name}
          </span>
        )}

        {/* Nome */}
        <h3 className="text-sm mt-2">{product.name}</h3>

        {/* Preço */}
        <p className="text-sm font-semibold">R$ {product.price.toFixed(2)}</p>

        {/* Botão */}
        <button className="w-full mt-2 bg-black text-white text-sm py-1 rounded">
          Adicionar
        </button>
      </div>
    );
  }