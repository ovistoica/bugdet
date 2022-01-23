# Budget manager

## Stack:

- CLJS
- Reagent
- Re-frame
- TailwindCSS

## Getting started

```shell
# install dependencies 

npm run install

# then

npm run dev
```

# Task:

0. Make a personal financial accounting gadget
1. Make 1 page on clojurescript + any library for html
2. The page should contain:
    - table of transactions (transaction fields: who was paid, how much) Currency is always: UAH
    - 2 input fields for creating a new transaction
3. A graph that shows costs by month
4. A separate Gauge widget that shows the total spending for the current month

Will be a plus:

1. If custom code (atom or something else) is used to manage the state
2. One-way dataflow and concentration of logic in one place (by analogy with redux)
3. There will be a link to the working application (so as not to deploy locally)
4. The application will be deployed on the online service repl.it or similar
