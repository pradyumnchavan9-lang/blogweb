import { useEffect,useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/api";
import "./ArticleDetails.css";

function ArticleDetails(){

    const {id} = useParams();
    const [article,setArticle] = useState(null);
    const [comment,setComment] = useState("");


    useEffect(()=>{
        fetchArticle();
    },[]);

    async function fetchArticle(){
        const response = await api.get(`/article/${id}`);
        setArticle(response.data);
    }

    async function handleCommentSubmit(e){
        e.preventDefault();
        await api.post(`/article/${id}/comments`,{
            content : comment
        });

        setComment("");
        fetchArticle();
    }

    if(!article) return <p className = "loading-text"> Loading... </p>;


    return(
            <div className="article-container">
                <div className="article-card">
                    <h2 className="article-title"> Article </h2>

                <div className="article-summary">
                    <b>Summary</b>
                    <p>{article.summary}</p>
                </div>

                <div className="article-content">
                    <p>{article.content}</p>
                </div>

                    <h3 className="comments-title">Comments</h3>

                <div className="comments-section">
                    {
                        article?.comments?.map(c=>(
                            <div key={c.id} className="comment-card">
                                <span className="comment-author">
                                    {c.author.username}
                                </span>
                                <span className="comment-text">
                                    {c.content}
                                </span>
                            </div>
                        ))
                    }
                </div>

                <hr className="article-divider" />

                      <form onSubmit={handleCommentSubmit} className = "comment-form">
                            <input
                              type="text"
                              placeholder="Add comment"
                              value={comment}
                              onChange={(e) => setComment(e.target.value)}
                            />
                            <button type="submit" className="comment-button">Post</button>
                      </form>

                </div>
            </div>
    )
}

export default ArticleDetails;