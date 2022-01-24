const defaultTheme = require('tailwindcss/defaultTheme')
const colors = require('tailwindcss/colors')


module.exports = {
    mode: 'jit',
    purge: {
        // in prod look at shadow-cljs output file in dev look at runtime, which will change files that are actually compiled; postcss watch should be a whole lot faster
        content: process.env.NODE_ENV === 'production' ? ["./public/js/main.js"] : ["./public/js/cljs-runtime/*.js"]
    },
    darkMode: false, // or 'media' or 'class'
    theme: {
        extend: {
            colors: {
                cyan: colors.cyan,
                'warm-gray': colors.warmGray,
            },
            fontFamily: {
                sans: ["Inter var", ...defaultTheme.fontFamily.sans],
            },
        },
    },
    variants: {},
    plugins: [
        require('@tailwindcss/forms'),
    ],
}
