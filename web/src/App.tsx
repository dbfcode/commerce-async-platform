import { BrowserRouter, Routes, Route } from "react-router-dom"
import Navbar from "./components/Navbar"
import HomePage from "./app/HomePage/page"
import CheckoutPage from "./app/CheckoutPage/page"
import EmailConfirmPage from "./app/EmailConfirmPage/page"
 
export default function App() {
  return (  
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/email-confirm" element={<EmailConfirmPage />} />
      </Routes>
    </BrowserRouter>
  )
}
 