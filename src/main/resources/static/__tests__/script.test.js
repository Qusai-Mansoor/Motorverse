// Import the functions to test
const script = require('../script');

// Mock the DOM elements and environment
document.body.innerHTML = `
  <div id="menu-toggle"></div>
  <div id="mobile-menu" class="hidden"></div>
  <div id="user-detail">
    <img id="avatar" />
    <div id="user-dropdown" class="hidden"></div>
  </div>
  <a id="register-link"></a>
  <a id="login-link"></a>
  <a id="mobile-register-link"></a>
  <a id="mobile-login-link"></a>
  <a id="return-vehicle-link" class="hidden"></a>
  <a id="mobile-return-vehicle-link" class="hidden"></a>
`;

// Mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: jest.fn(key => store[key] || null),
    setItem: jest.fn((key, value) => {
      store[key] = value.toString();
    }),
    removeItem: jest.fn(key => {
      delete store[key];
    }),
    clear: jest.fn(() => {
      store = {};
    })
  };
})();
Object.defineProperty(window, 'localStorage', {
  value: localStorageMock
});

describe('Menu Toggle Functionality', () => {
  beforeEach(() => {
    document.getElementById('mobile-menu').classList.add('hidden');
  });
  
  test('toggles mobile menu visibility when clicked', () => {
    // Simulate document loaded event
    document.dispatchEvent(new Event('DOMContentLoaded'));
    
    // Get the menu toggle button
    const menuToggle = document.getElementById('menu-toggle');
    
    // Simulate click event
    menuToggle.click();
    
    // Check if mobile menu is visible
    expect(document.getElementById('mobile-menu').classList.contains('hidden')).toBe(false);
    
    // Click again to hide
    menuToggle.click();
    
    // Check if mobile menu is hidden
    expect(document.getElementById('mobile-menu').classList.contains('hidden')).toBe(true);
  });
});

describe('User Authentication UI', () => {
  beforeEach(() => {
    // Reset localStorage
    localStorageMock.clear();
    
    // Reset UI
    document.getElementById('register-link').classList.remove('hidden');
    document.getElementById('login-link').classList.remove('hidden');
    document.getElementById('return-vehicle-link').classList.add('hidden');
  });
  
  test('shows login/register links when user is not logged in', () => {
    // Simulate document loaded event
    document.dispatchEvent(new Event('DOMContentLoaded'));
    
    // Check if login and register links are visible
    expect(document.getElementById('register-link').classList.contains('hidden')).toBe(false);
    expect(document.getElementById('login-link').classList.contains('hidden')).toBe(false);
    expect(document.getElementById('return-vehicle-link').classList.contains('hidden')).toBe(true);
  });
  
  test('hides login/register links when user is logged in', () => {
    // Set mock logged in user
    const mockUser = {
      id: 1,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com'
    };
    localStorageMock.setItem('loggedInUser', JSON.stringify(mockUser));
    localStorageMock.setItem('userToken', 'fake-token');
    
    // Simulate document loaded event
    document.dispatchEvent(new Event('DOMContentLoaded'));
    
    // Check if login and register links are hidden
    expect(document.getElementById('register-link').classList.contains('hidden')).toBe(true);
    expect(document.getElementById('login-link').classList.contains('hidden')).toBe(true);
    expect(document.getElementById('return-vehicle-link').classList.contains('hidden')).toBe(false);
  });
});

// Additional tests for other frontend functionalities
// ...
