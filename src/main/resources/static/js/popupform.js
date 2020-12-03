function clickPicButton() {

    var picRow = document.getElementById("changePicRow");
    var nameRow = document.getElementById("changeNameRow");
    var bioRow = document.getElementById("changeBioRow");

    picRow.style.display = "block";
    nameRow.style.display = "none";
    bioRow.style.display = "none";

}

function clickNameButton() {

    var picRow = document.getElementById("changePicRow");
    var nameRow = document.getElementById("changeNameRow");
    var bioRow = document.getElementById("changeBioRow");

    picRow.style.display = "none";
    nameRow.style.display = "block";
    bioRow.style.display = "none";

}


function clickBioButton() {

    var picRow = document.getElementById("changePicRow");
    var nameRow = document.getElementById("changeNameRow");
    var bioRow = document.getElementById("changeBioRow");

    picRow.style.display = "none";
    nameRow.style.display = "none";
    bioRow.style.display = "block";

}
