module.exports = {
  content: [
    // Example content paths...
    './src/**/*.html',
  ],
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
