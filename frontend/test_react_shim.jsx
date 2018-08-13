//This file gets rid of the annoying Warning: React depends on requestAnimationFrame. warnings in tests.
//see https://github.com/facebook/jest/issues/4545

global.requestAnimationFrame = (callback) => {
    setTimeout(callback, 0);
};