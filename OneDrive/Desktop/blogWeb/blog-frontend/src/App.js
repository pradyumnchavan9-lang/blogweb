import './App.css';
import ArticleList from './ArticleList';
function App() {

    const articles =[
                        { id: 1, title: "How to learn DSA" },
                        { id: 2, title: "Spring Boot for Beginners" },
                        { id: 3, title: "React from Scratch" }
                    ]

  return (
           <div>
                <h1>Articles</h1>
                 <ArticleList articles = {articles} />
          </div>
  );
}

export default App;
