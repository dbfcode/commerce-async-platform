export default function ProductCardSkeleton() {
  return (
    <div className="bg-surface rounded-lg p-3 shadow-sm border border-border animate-pulse">
      {/* Imagem */}
      <div className="h-20 bg-gray-200 rounded mb-3"></div>

      {/* Categoria */}
      <div className="h-4 w-16 bg-gray-200 rounded mb-2"></div>

      {/* Nome */}
      <div className="h-4 w-28 bg-gray-200 rounded mb-2"></div>

      {/* Preço */}
      <div className="h-4 w-20 bg-gray-200 rounded mb-3"></div>

      {/* Botão */}
      <div className="h-8 w-full bg-gray-300 rounded"></div>
    </div>
  );
}