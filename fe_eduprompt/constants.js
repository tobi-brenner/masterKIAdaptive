// config.js

// Environment variables should be loaded here, ensuring they are available throughout the app
const BE_BASE_URL = process.env.BE_URL || 'http://localhost:8080';

module.exports = {
    BE_BASE_URL,
};