import AuthClient from "./AuthClient";
import ClientConfig from "./ClientConfig";
import User from "../dto/User";
import UserSkillDTO from "../dto/UserSkillDTO";

class ApiClient {

    static POST_QUESTIONNAIRE = '/questionnaire';
    static GET_CURRENT_USER = '/user';
    static POST_ADD_ARTICLE_READ = '/user/addCompletedMaterial';
    static POST_ADD_LIKE = '/material/';
    static POST_REMOVE_LIKE = '/material/';
    static GET_ARTICLE_LIST = '/article/list';
    static GET_ARTICLE_BY_ID = '/article/';
    static GET_COURSE_LIST = '/courses';
    static GET_COURSE_BY_ID = '/courses/';
    static GET_JOB_BY_ID = '/vacancies/';
    static GET_USER_SKILLS = '/skills';
    static GET_ALL_SKILL_NAMES = '/skills/names';
    static POST_UPDATE_SKILLS = '/skills';
    static DELETE_SKILL = '/skills/remove';
    static GET_TRACK_LATEST = '/tracks/latest';
    static GET_TRACKS = '/tracks';
    static POST_TRACK_GENERATE = '/tracks/generate';
    static DELETE_TRACK = '/tracks/';

    static sendQuestionnaire(name: string): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_QUESTIONNAIRE, {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN},
            body: JSON.stringify({name: name})
        });
    }

    static getCurrentUser() {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_CURRENT_USER, {
            method: 'GET', headers: {'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static putCurrentUser(user: User): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_CURRENT_USER, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN},
            body: JSON.stringify({
                college: user.college,
                fullName: user.fullName,
                city: user.city,
                birthdayYear: user.birthdayYear,
                desiredPosition: user.desiredPosition
            })
        });
    }

    static getArticles(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_ARTICLE_LIST, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static getArticle(id: number): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_ARTICLE_BY_ID + id, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static getCourses(search: string): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_COURSE_LIST + '?search=' + search, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static getCourse(id: number): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_COURSE_BY_ID + id, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static getJob(id: number): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_JOB_BY_ID + id, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static materialCompleted(id): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_ADD_ARTICLE_READ, {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN},
            body: JSON.stringify({id: parseInt(id)})
        });
    }

    static learningMaterialLike(id): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_ADD_LIKE + id + '/like', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static learningMaterialUnlike(id): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_REMOVE_LIKE + id + '/remove-like', {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static getTracks(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_TRACKS, {
            method: 'GET', headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static getLatestTrack(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_TRACK_LATEST, {
            method: 'GET', headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static generateNewTrack(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_TRACK_GENERATE, {
            method: 'POST', headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }

    static requestSkills(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_USER_SKILLS, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static getAllSkillNames(): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.GET_ALL_SKILL_NAMES, {
            method: 'GET', headers: {
                'Content-Type': 'application/json',
                'Authorization': (AuthClient.ACCESS_TOKEN != null) ? AuthClient.ACCESS_TOKEN : ''
            }
        });
    }

    static updateSkills(userSkillDto: UserSkillDTO[]): Promise<Response> {
        console.log(JSON.stringify(userSkillDto))
        return fetch(ClientConfig.SERVER_LINK + ApiClient.POST_UPDATE_SKILLS, {
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN},
            body: JSON.stringify(userSkillDto)
        });
    }

    static deleteSkill(id): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.DELETE_SKILL, {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN},
            body: JSON.stringify({id: parseInt(id)})
        });
    }

    static deleteTrackById(trackId): Promise<Response> {
        return fetch(ClientConfig.SERVER_LINK + ApiClient.DELETE_TRACK + trackId, {
            method: 'DELETE',
            headers: {'Content-Type': 'application/json', 'Authorization': AuthClient.ACCESS_TOKEN}
        });
    }
}

export default ApiClient;
