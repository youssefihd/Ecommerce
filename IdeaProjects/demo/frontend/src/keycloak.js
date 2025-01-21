import Keycloak from 'keycloak-js';

let keycloakInstance = null;

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'demo',
  clientId: 'front-end',
});

export const getKeycloakInstance = () => {
  if (!keycloakInstance) {
    keycloakInstance = keycloak;
  }
  return keycloakInstance;
};

export const Logout = () => {
  keycloak.logout();
};

export const GetToken = async () => {
  await keycloak.updateToken(30);
  return keycloak.token;
};

export const initKeycloak = () => {
  return keycloak.init({ onLoad: 'login-required' });
};

export default keycloak;
