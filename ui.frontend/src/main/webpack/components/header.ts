// Header behaviour, added on top of markup that already works without JavaScript:
// - phones and tablets: the Menu button shows and hides the navigation (without this script it is always shown);
// - the dropdown menus are <details> elements; this closes them with Escape or a click outside.

const COLLAPSIBLE = 'cmp-header--collapsible';
const OPEN = 'cmp-header--open';

function setNavOpen(header: Element, toggle: HTMLButtonElement, open: boolean): void {
    header.classList.toggle(OPEN, open);
    toggle.setAttribute('aria-expanded', String(open));
}

function initHeader(header: Element): void {
    const toggle = header.querySelector<HTMLButtonElement>('.cmp-header__toggle');
    if (toggle) {
        toggle.hidden = false;
        header.classList.add(COLLAPSIBLE);
        toggle.addEventListener('click', () => setNavOpen(header, toggle, !header.classList.contains(OPEN)));
    }

    header.addEventListener('keydown', (event: KeyboardEvent) => {
        if (event.key !== 'Escape') {
            return;
        }
        const menu = (event.target as Element).closest<HTMLDetailsElement>('.cmp-header__menu[open]');
        if (menu) {
            menu.open = false;
            menu.querySelector('summary').focus();
        } else if (toggle && header.classList.contains(OPEN)) {
            setNavOpen(header, toggle, false);
            toggle.focus();
        }
    });

    document.addEventListener('click', (event: MouseEvent) => {
        header.querySelectorAll<HTMLDetailsElement>('.cmp-header__menu[open]').forEach((menu) => {
            if (!menu.contains(event.target as Node)) {
                menu.open = false;
            }
        });
    });
}

document.querySelectorAll('.cmp-header').forEach(initHeader);
