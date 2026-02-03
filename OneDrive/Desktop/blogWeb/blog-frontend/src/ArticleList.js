function ArticleList(props){
    return(
        <ul>
        {props.articles.map(article => (
            <li key ={article.id}>
                {article.title}
            </li>
        ))}
       </ul>
    );
}

export default ArticleList;