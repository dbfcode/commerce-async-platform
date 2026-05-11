import { useState } from "react";

export default function Navbar() {
  // Controla se o menu mobile está aberto ou fechado
  const [isOpen, setIsOpen] = useState(false);

  return (
    <nav className="bg-surface border-b border-border">
      <div className="flex items-center justify-between px-2 h-13">
        <div className="flex items-center gap-1">
          <div className="w-2 h-2 bg-primary rounded-full"></div>
          <span>OrderFlow</span>
        </div>

        {/* Botão hambúrguer (lado direito, só aparece no mobile) */}
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="md:hidden p-2 rounded-md hover:bg-gray-100"
          aria-label="Abrir menu"
        >
          {/* Troca o ícone dependendo do estado */}
          {isOpen ? (
            // Ícone X (fechar)
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          ) : (
            // Ícone hambúrguer (3 linhas)
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          )}
        </button>

        {/* Links no desktop (some no mobile) */}
        <div className="hidden md:flex items-center gap-4">
          <a href="#" className="hover:text-primary">Início</a>
          <a href="#" className="hover:text-primary">Pedidos</a>
          <a href="#" className="hover:text-primary">Configurações</a>
        </div>
      </div>

      {/* Menu mobile — só aparece quando isOpen for true */}
      {isOpen && (
        <div className="md:hidden flex flex-col px-4 pb-4 gap-3 border-t border-border">
          <a href="/" className="hover:text-primary">Produtos</a>
          <a href="/checkout" className="hover:text-primary">Checkout</a>
          <a href="/email-confirm" className="hover:text-primary">Pedido</a>
        </div>
      )}
    </nav>
  );
}