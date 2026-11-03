# AI notes

## Tools and what I used them for

| Tool | Used for |
|---|---|
| **GitHub Copilot app** (agent mode, Claude model) | Generating the project from the archetype; first drafts of the Sling Models, unit tests, HTL, dialogs, policies, SCSS and these documents; running Maven builds and reading `error.log` to diagnose deploy problems. |
| **Playwright** with Microsoft Edge (scripts written with Copilot) | Checking the page on the local author at 375 px and 1280 px: heading order, landmarks, keyboard use of the accordion, console errors, and the screenshots in `docs/images`. |
| **AEM's own consoles** (CRXDE Lite, the OSGi Bundles console, the page editor) | Checking what the AI said against what AEM actually did. |

The project rules I gave the assistant are in [`.github/copilot-instructions.md`](.github/copilot-instructions.md), with `AGENTS.md` and `CLAUDE.md` pointing to them. I reviewed every change before committing it.

## One thing the AI got wrong, and how I caught it

The header and footer used placeholder links such as `https://www.example.com/help`. In the editor screenshots, those links had broken-link markers. The AI said the markers only appear in edit mode and could be ignored. Checking the published view (`?wcmmode=disabled`) showed that "Help" and the footer links weren't links at all, only text. AEM's link checker had requested those URLs, got 404 responses, and removed the `<a>` tags outside edit mode. I pointed the placeholders at `https://www.example.com/`, which returns 200, and added a rule about it to the context file.

**Lesson:** check AI explanations against the rendered output, not just the screenshot the AI is describing.

## One suggestion I rejected, and why

After the first deploy, components failed because my local SDK had two Core Components bundles: 2.19 from another project on the same instance, and the SDK's own 2.28. The AI suggested uninstalling the other project's package. I rejected that because the instance is shared, and removing another project's code to make mine work is not reversible or my decision to make. Instead I stopped the older bundle in the Bundles console, which can be undone with one click, and documented the problem in the README's troubleshooting section. A clean SDK doesn't have the problem.

## What I would set up differently for a team of five on a client project

- **Approved tools only.** Use the client's approved AI tools on business licences that don't train on our code. Exclude secrets, OSGi configs with credentials and client content from AI context.
- **Shared, reviewed context files.** One `copilot-instructions.md`/`AGENTS.md` in the repository, owned by the tech lead and changed by pull request like code. Add rules there whenever review catches the same AI mistake twice.
- **Same versions for everyone.** Pin the SDK, Java and Node versions in the repository so people and AI tools build against the same thing.
- **Human review and CI as the gate.** Every AI-assisted change goes through a pull request with a named reviewer who can explain it. CI runs the Maven build, unit tests, Cloud Manager code-quality rules, and Playwright with axe accessibility checks. The pull request template asks which requirement IDs are covered and what was checked by hand.
- **Shared prompts for repeated work.** For example, "new component with model, test, dialog and policy", kept in the repository so results are consistent across the team.
