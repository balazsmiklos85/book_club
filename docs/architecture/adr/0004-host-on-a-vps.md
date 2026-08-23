# Host on a VPS

* Status: accepted
* Date: 2026-08-23

## Context

The Java original runs on Heroku, which costs $12–15 per month. It serves around 20 users, a number that will never grow enough to need scaling.

The rewrite needs modest hosting: one always-on process, a database for both data and job queue. Whatever host runs the app also carries its state.

Heroku serves the current app well: deployments integrate with GitHub and run smoothly. But it paywalls what may come next: persisting logs costs extra, and getting metrics into something like Grafana costs extra too.

AWS Lambda was seriously weighed and fell to economics: stateless functions still need a managed database to back their state, and [an earlier record](0002-use-mvc.md) already concluded that the database alone would exceed this app's entire hosting budget.

## Decision

We will host the app on a VPS.

Heroku did not lose on quality. Its GitHub integration and deployment flow are genuinely good, and it runs the Java original without complaint. It lost on fit. For roughly 20 users who will never scale up, its robustness buys nothing. The capabilities I want next, persistent logs, metrics on a Grafana dashboard, sit behind paywalls, while on a VPS they can run on the already-paid instance for the same monthly price.

Lambda's loss stands as stated in the context: stateless compute still needs a stateful database, and that alone busts the budget.

How the app actually ships to the VPS, container or service, pipeline, or script, is deliberately left open. It may earn its own record once tried.

## Consequences

### Easier

* Costs stay flat, with headroom: additional observability or future side projects can run on the same instance instead of additional services that require extra payment.
* State and compute share one machine: the database sits next to the app, with no extra database service to pay for.

### Harder

* Heroku's GitHub-integrated deployments are gone. Shipping code becomes a self-built mechanism, still to be chosen and possibly recorded separately.
* Operations become my job: operating system patching, TLS certificates, and above all backups.
* One VPS is a single point of failure. Heroku-grade robustness is traded away knowingly.
