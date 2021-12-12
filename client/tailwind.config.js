module.exports = {
  purge: [],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      colors: {
        primary: '#25040E',
        action: '#267DFF',
      },
    },
  },
  variants: {
    extend: {
      textColor: ['group-focus'],
      textOpacity: ['group-focus'],
      animation: ['hover', 'group-hover']
    },
  },
  plugins: [],
}
