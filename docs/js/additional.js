class LoadingScreen {
  static loaded() {
    const loadingScreen = document.querySelector(".loadingScreen");
    loadingScreen.style.opacity = "0";
    setTimeout(() => {
      loadingScreen.style.display = "none";
    }, 400);
  }
  static unload(){
    const loadingScreen = document.querySelector(".loadingScreen");
    loadingScreen.style.opacity = "1";
    loadingScreen.style.display = "flex";
  }
}
// Dark Mode
const preference = localStorage.getItem("dark");
if (preference == "true") {
  document.body.classList.add("dark");
} else if (
  preference !== "false" &&
  window.matchMedia &&
  window.matchMedia("(prefers-color-scheme: dark)").matches
) {
  document.body.classList.add("dark");
}
function toggleDark() {
  const dark = document.body.classList.contains("dark");
  document.body.classList.toggle("dark");
  localStorage.setItem("dark", !dark);
}
// Detect Touch to prevent colouring Dark/Light Button
function detecttouch() {
  document.getElementById("toggledark").classList.remove("focustoggle");
}

document.onmousedown = function (e) {
  multilingual.langEvent(e)
}
document.onkeyup = function (e) {
  if (e.target.parentElement.id != "langOption" || e.keyCode == 27){
    document.body.classList.remove("visible")
  }
  if (e.keyCode == 13){
    multilingual.langEvent(e)
    document.getElementById("langOption").firstElementChild.focus() 
  }
}
