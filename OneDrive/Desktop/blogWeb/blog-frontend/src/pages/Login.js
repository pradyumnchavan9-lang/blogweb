import { useState } from "react";


function Login() {

    const [username,setUsername] = useState("");
    const [password,setPassword] = useState("");

    function handleSubmit(e){
        e.preventDefault();
        console.log("Username: ", username);
        console.log("Password: ", password);

    }

    return(
        <div>
            <h2>Login</h2>

            <form onSubmit={handleSubmit}>
                <input
                    type = "text"
                    placeholder = "Username"
                    value = {username}
                    onChange = {(e) => setUsername(e.target.value)}
                />

                <br /><br />

                <input
                    type = "password"
                    placeholder = "Password"
                    value = {password}
                    onChange = {(e) => setPassword(e.target.value)}
                />

                <br /><br />

                <button type ="submit">Login</button>
            </form>
        </div>
    );

}

export default Login;
