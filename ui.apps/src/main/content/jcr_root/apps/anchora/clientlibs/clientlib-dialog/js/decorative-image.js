/*
 * Hero and Teaser dialogs: hides "Alternative text" while "Image is decorative" is ticked,
 * because the page ignores it then. The text is kept, so unticking the box shows it again.
 */
(function (document, $, Coral) {
    "use strict";

    var CHECKBOX = "coral-checkbox.anchora-decorative";

    function toggleAlt(checkbox) {
        $(checkbox).closest("form").find("[name='./alt']")
            .closest(".coral-Form-fieldwrapper").toggle(!checkbox.checked);
    }

    $(document).on("foundation-contentloaded", function (event) {
        $(event.target).find(CHECKBOX).each(function () {
            var checkbox = this;
            Coral.commons.ready(checkbox, function () {
                toggleAlt(checkbox);
            });
        });
    });

    $(document).on("change", CHECKBOX, function () {
        toggleAlt(this);
    });
}(document, Granite.$, Coral));
