const menuToggle = document.querySelector(".menu-toggle");
const nav = document.querySelector(".nav");
const searchButton = document.querySelector(".search-button");
const searchPanel = document.querySelector(".search-panel");
const searchInput = document.querySelector("#search-input");
const closeSearch = document.querySelector(".search-field button");

menuToggle.addEventListener("click", () => {
  const isOpen = nav.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded", String(isOpen));
  menuToggle.setAttribute("aria-label", isOpen ? "Fechar menu" : "Abrir menu");
});

nav.addEventListener("click", (event) => {
  if (event.target.matches("a")) {
    nav.classList.remove("open");
    menuToggle.setAttribute("aria-expanded", "false");
  }
});

searchButton.addEventListener("click", () => {
  const isOpen = searchPanel.classList.toggle("open");
  searchPanel.setAttribute("aria-hidden", String(!isOpen));

  if (isOpen) {
    searchInput.focus();
  }
});

closeSearch.addEventListener("click", () => {
  searchPanel.classList.remove("open");
  searchPanel.setAttribute("aria-hidden", "true");
  searchButton.focus();
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    searchPanel.classList.remove("open");
    searchPanel.setAttribute("aria-hidden", "true");
    nav.classList.remove("open");
    menuToggle.setAttribute("aria-expanded", "false");
  }
});
