const WEB_PROTOCOL = process.env.REACT_APP_PROTOCOL || 'http';
const SERVER_HOST_PORT = process.env.REACT_APP_BACKEND_HOST || 'localhost:8080';

class ClientConfig {
    static SERVER_LINK = WEB_PROTOCOL + '://' + SERVER_HOST_PORT;
}

export default ClientConfig;
