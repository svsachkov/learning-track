class HeadHunterClient {

    static URL = 'https://api.hh.ru/vacancies?text=';

    static getVacancies(text: string): Promise<Response> {
        return fetch(HeadHunterClient.URL + text, {
            method: 'GET', headers: {'Content-Type': 'application/json'}
        });
    }
}

export default HeadHunterClient;
