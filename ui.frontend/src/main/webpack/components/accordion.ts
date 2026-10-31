// "Expand all items" button of the accordion section. accordion.html renders it hidden; this script shows it.
// The Core Components accordion script owns the open or closed state of each item, so this script only clicks the
// item buttons that need to change. Core's click handler also moves focus to each item and writes its id into the
// URL, so both are put back afterwards.

// Core keeps the state in data-cmp-expanded on the item. It sets aria-expanded a moment later (in a setTimeout),
// so aria-expanded is not reliable straight after a click.
function isExpanded(button: Element): boolean {
    return button.closest('.cmp-accordion__item').hasAttribute('data-cmp-expanded');
}

function initAccordion(accordion: Element): void {
    const toggle = accordion.querySelector<HTMLButtonElement>('.cmp-accordion__toggle-all');
    if (!toggle) {
        return;
    }
    // Only this accordion's items, not those of an accordion inside one of its panels.
    const buttons: HTMLButtonElement[] = Array.prototype.filter.call(
        accordion.querySelectorAll('.cmp-accordion__button'),
        (button: Element) => button.closest('.cmp-accordion') === accordion);

    function allExpanded(): boolean {
        return buttons.every(isExpanded);
    }

    function updateLabel(): void {
        toggle.textContent = allExpanded() ? toggle.dataset.collapseLabel : toggle.dataset.expandLabel;
    }

    toggle.hidden = false;
    toggle.addEventListener('click', () => {
        const expand = !allExpanded();
        const url = window.location.href;
        buttons.filter((button) => isExpanded(button) !== expand).forEach((button) => button.click());
        window.history.replaceState(window.history.state, '', url);
        toggle.focus();
    });

    // Runs after Core's own listener on the item button, so data-cmp-expanded is already up to date.
    accordion.addEventListener('click', (event: MouseEvent) => {
        if ((event.target as Element).closest('.cmp-accordion__button')) {
            updateLabel();
        }
    });
}

document.querySelectorAll('.cmp-accordion').forEach(initAccordion);
