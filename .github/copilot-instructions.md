# Copilot instructions – Anchora (AEM as a Cloud Service)

These rules apply to every change in this repository. Read `AGENTS.md` for the module layout and build commands, and `README.md` for the component reference and design decisions.

## What this project is

- One page ("Flying with infants and children") on AEMaaCS, built with Hero, Teaser and Accordion components on an editable template with a locked header and footer.
- Java package `com.anchora.aem.core`; app ID `anchora`; site root `/content/anchora/us/en`.
- Keep the code small enough that the author can explain every line. Prefer one clear way of doing something over a generic framework.

## Java and Sling Models (`core`)

- All rendering logic lives in Sling Models. HTL only reads getters; no business logic, string building or conditions beyond `data-sly-test` on a getter.
- Each model has a public interface in `com.anchora.aem.core.models` and an implementation in `…models.impl`. Adapt from `SlingHttpServletRequest`, set `resourceType`, and use `DefaultInjectionStrategy.OPTIONAL` with sensible defaults.
- Reuse the helpers `Links` (internal page → `.html`, new tab → `target="_blank"` and `rel="noopener noreferrer"`) and `ResponsiveImages` (srcset from `cq5dam.web.*` renditions, alt text falling back to `dc:description`, decorative flag). Don't add a second way of doing either.
- To extend a Core Component, use the delegation pattern (`@Self @Via(type = ResourceSuperType.class)`), as `SectionAccordionImpl` does.
- No new runtime dependencies without asking. Compile against the `aem-sdk-api` version pinned in the root `pom.xml`.

## Tests

- Every model and helper has a JUnit 5 test in `core/src/test/java/…/models/impl`, using `AppAemContext` (wcm.io AEM Mocks) and the Core Components mock plugin.
- Test behaviour an author would notice (defaults, empty states, policy limits, link and alt-text rules), with descriptive method names such as `policyCannotMakeTheTeaserAnH1`.
- `mvn clean install` must pass with no failing tests before a commit.

## Components and templates (`ui.apps`, `ui.content`)

- Components live in `/apps/anchora/components`. Use Core Component proxies (`sling:resourceSuperType`) where Core already does the job.
- Content components go in the group "Anchora - Content"; header and footer in "Anchora - Structure". Page-level components stay in `.hidden`.
- Heading levels come from policies, not dialogs. The Hero title is the only H1. Title is H2–H6; Teaser default H2; accordion section heading H2 and items H3.
- Dialogs: short, plain-English labels and a `fieldDescription` that tells the author what to enter (sizes, examples). Mark truly required fields `required="{Boolean}true"`.
- Lists of links are composite multifields read with `Links.fromMultifield`. A list inside a list (footer columns and their links) is a nested composite multifield, stored as `columns/item0/links/item0`.
- A layout choice made per component on the page (such as the teaser's image position) is a dialog field read by the model, with a safe default and a test. Keep the Style System for site-wide looks.
- The page template `page-content` keeps header and footer as locked experience fragments, a Core breadcrumb (unlocked, "Page Breadcrumb" policy, `startLevel=3`) at the top of `<main>`, and one unlocked container that allows only hero, teaser, accordion, title and text. Change allowed components in the "Page Content" policy.
- `ui.content` filters are `mode="merge"`. A redeploy won't overwrite existing nodes, and won't add new children written inside an existing node's `.content.xml` (for example a new policy). Delete the node that owns the file (the whole page or `policies` node) if the shipped version must replace it. A new DAM folder needs its own filter root in `META-INF/vault/filter.xml`.
- Placeholder external links must resolve (use `https://www.example.com/`). AEM's link checker removes anchors to URLs that return errors.

## Front end (`ui.frontend`)

- All CSS comes from `ui.frontend` and ships as the `anchora.site` client library. No inline `style` attributes, no `<script>` blocks, no CSS or JS in `ui.apps` except Core clientlibs embedded in `anchora.base`.
- SCSS is mobile-first. Use the variables and mixins in `site/_variables.scss` (`tablet-up`, `desktop-up`, `focus-ring`, `chevron`, `arrow-icon`). Component styles go in `components/_<name>.scss`. Check at 375 px and 1280 px with no horizontal scroll.
- Class names follow Core's BEM style: `cmp-<component>__<element>--<modifier>`.
- Icons are SVG data URIs used as CSS masks (`arrow-icon`, `chevron`, and the footer's `$social-icons` map), so they take the text colour. Keep the licence comment when copying an icon from an icon set.
- No custom JavaScript unless Core can't do it. The accordion uses the Core `accordion.v1` script.

## Accessibility (WCAG 2.2 AA)

- One `<header>`, one `<main id="main-content">`, one `<footer>`; every `<nav>` has an `aria-label`. The Core page skip link targets `#main-content`.
- Images: authored alt text, else the DAM description, else empty `alt=""` when marked decorative. Always output `width` and `height`.
- Links that open in a new tab get a visually hidden `<span class="cmp-link__screen-reader-only"> (opens in a new tab)</span>`. Use exactly this class; Core's link script adds a second span otherwise.
- Every interactive element has a visible `:focus-visible` outline.
- Text and links in the brand coral use `$color-accent-text`. `$color-accent` is for decoration only; its contrast on white is too low for text.

## Writing

- British English, plain and short. Copy on the page is paraphrased placeholder text for a fictional brand ("Anchora"); never paste real airline content, client code or credentials.
- Keep `README.md` (developers) and `AUTHORING.md` (authors) up to date in the same change as the code they describe.
- Commit messages: imperative mood, one change per commit, and the module in brackets where useful, for example "Add teaser link tests (core)".
