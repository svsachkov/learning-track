import ClientConfig from "./ClientConfig";

class AuthClient {

    static AUTH_ENDPOINT = '/auth'
    static LOGIN_ENDPOINT = '/auth/login';
    static REGISTER_ENDPOINT = '/auth/register';

    static USERNAME = JSON.parse(localStorage.getItem('username')) || null;
    static ACCESS_TOKEN = JSON.parse(localStorage.getItem('sessionId')) || null;

    static auth(username: string, password: string): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + AuthClient.AUTH_ENDPOINT, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username: username, password: password})
        })
    }

    static login(username: string, password: string): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + AuthClient.LOGIN_ENDPOINT, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username: username, password: password})
        })
    }

    static register(username: string, password: string): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + AuthClient.REGISTER_ENDPOINT, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({username: username, password: password})
        })
    }

}

export default AuthClient;
