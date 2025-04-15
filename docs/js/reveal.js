var revealObjects
async function reveal() {
    revealObjects = document.getElementsByClassName("is-revealing")
    for (let a = 0; a < revealObjects.length; a++) {
        boundings = revealObjects[a].parentElement.getBoundingClientRect();
        if ((boundings.height - boundings.top < -250) && (boundings.top - window.innerHeight) > (0 - (boundings.height) * 0.35)) {
            revealObjects[a].classList.remove("visible")
            revealObjects[a].classList.add("invisible")
        }
        if ((boundings.top - window.innerHeight) < (0 - (boundings.height) * 0.35)) {
            revealObjects[a].classList.add("visible")
            revealObjects[a].classList.remove("invisible")
        }
    }
}
window.onscroll = function (e) {
    reveal()
}
