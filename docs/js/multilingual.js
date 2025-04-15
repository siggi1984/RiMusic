class Multilingual {
  language;
  defaultValues = {};
  selectedValues = {};
  constructor(supported) {
    const user = (navigator.language || navigator.userLanguage).split("-")[0];
    if (supported.includes(user)) {
      this.language = localStorage.getItem("language") || user || "en";
    } else {
      this.language = localStorage.getItem("language") || "en";
    }
  }

  _decode(string) {
    return string
      .replace(/\\n/g, "\n")
      .replaceAll("\\'", "'")
      .replaceAll('\\"', '"')
      .replaceAll("\\\\", "\\")
      .replaceAll("\\t", "\t");
  }
  async loadStrings(lang) {
    var language = this.language
    if (lang) language = lang;
    const selectedValuesResponse = await fetch(
      `res/values-${language}/strings.xml`,
    );
    var selectedValues = await selectedValuesResponse.text();

    const parserSelected = new DOMParser();
    const xmlDocSelected = parserSelected.parseFromString(
      selectedValues,
      "text/xml",
    );
    const stringsSelected = xmlDocSelected.getElementsByTagName("string");
    for (const string of stringsSelected) {
      const name = string.getAttribute("name");
      const value = this._decode(string.textContent);
      this.selectedValues[name] = value;
    }
    if (lang)
      this.updateDom();
  }
  getString(name) {
    return this.selectedValues[name] || this.defaultValues[name];
  }
  updateDom() {
    const allElements = Array.prototype.slice.call(
      document.body.getElementsByTagName("*"),
    );
    allElements.forEach((el) => {
      const name = el.getAttribute("name");
      if (name) {
        const value = this.getString(name) || "";
        el.innerHTML = value.replaceAll("\n", "<br>")
      }
    });
  }
  updateLanguage(el) {
    const lang = el.attributes.lang.value;
    localStorage.setItem("language", lang);
  }
  loadLanguageSelectInput(el) {
    const element = el.children[0].children[0].children[0];
    for (const option of element.children) {
      if (option.attributes.lang.value == this.language) {
        langSelect.childNodes[0].textContent = option.innerText;
        break;
      }
    }
  }
  setAttribute(el) {
    el.setAttribute("lang", this.language);
  }
  changeLang(a) {
    const lang = a.target.attributes.lang.value
    console.log("Language change to: " + lang)
    LoadingScreen.unload();
    langSelect.childNodes[0].textContent = a.target.innerText;
    langSel.setAttribute("value", lang);
    this.updateLanguage(a.target);
    this.loadStrings(lang);
    document.getElementsByTagName("a")[0].focus()
    setTimeout(() => {
      LoadingScreen.loaded();
    }, 400);
  }
  langEvent(e) {
    if (e.target.id == langSelect.id) {
      document.body.classList.toggle("visible");
      if (window.innerWidth < 640){
       }
      return;
    }
    if (e.target.parentElement){
      if (e.target.id == "globe" || e.target.parentElement.id == "globe"){
        document.body.classList.add("visible");
        if (window.innerWidth > 640)
          document.getElementById("langSelect").focus()
        return;
      }
    }
    if (e.target.id == langOption.id) {
      return;
    }
    if (e.target.parentElement) {
      if (e.target.parentElement.id == langOption.id) {
        this.changeLang(e);
      }
    }
    if (document.body.classList.contains("visible")){
      document.body.classList.remove("visible");
    }
  };
  onwheel = window.onwheel = function (e) {
    if (e.target.id == langSelect.id || e.target.attributes.lang) {
      return;
    }
    document.body.classList.remove("visible");
  }
}
