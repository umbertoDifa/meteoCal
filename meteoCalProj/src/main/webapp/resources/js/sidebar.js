$(document).ready(function () {
    $("#sidebar").css("left", "-300px");

    $("#hamburger").click(function () {
        if ($("#sidebar").css("left") == "0px") {
            $("#sidebar").animate({"left": "-300px"}, "slow");
        } else {
            $("#sidebar").animate({"left": "0px"}, "slow");
        }
    });
    
});