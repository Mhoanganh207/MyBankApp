function LoggedIn(){
    document.getElementById("account").classList.remove("hide");
    document.getElementById("login").classList.add("hide");
    document.getElementById("signup").classList.add("hide");
    document.getElementById("logo-anchor").href = "/account";
}