import { useState } from "react";

function Register() {

    const [username,setUsername] = useState("");
    const [password,setPassword] = useState("");
    const [email,setEmail] = useState("");

    function handleSubmit(e){
        e.preventDefault();
        console.log("Username: ",username);
        console.log("Password: ",password);
        console.log("Email: ",email);
    }

    return (
        <div>
            <form onSubmit = {handleSubmit}>
                <input
                    type = "text"
                    placeholder = "Username"
                    value = {username}
                    onChange = {(e) => setUsername(e.target.value)}
                />

                <br /><br />

                <input
                     type = "email"
                     placeholder = "Email"
                     value = {email}
                     onChange = {(e) => setEmail(e.target.value)}
                />

                <br /><br />

                <input
                    type = "password"
                    placeholder = "Password"
                    value = {password}
                    onChange = {(e) => setPassword(e.target.value)}
                />

                <br /><br />



                <button type ="submit">Register</button>
            </form>
        </div>
    );

}

export default Register;
