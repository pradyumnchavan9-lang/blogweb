import { Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Navbar from "./components/Navbar";
import CreateArticle from "./pages/CreateArticle.js";
import ArticleDetails from "./pages/ArticleDetails.js";

function App() {
  return (
  <>
    <Navbar />

    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/articles/new" element={<CreateArticle />} />
      <Route path="/articles/:id" element={<ArticleDetails />} />
    </Routes>

  </>
  );
}

export default App;
