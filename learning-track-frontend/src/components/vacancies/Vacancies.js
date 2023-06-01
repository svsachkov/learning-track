import * as React from "react";
import {Navigate} from "react-router-dom";
import HashLoader from "react-spinners/HashLoader";

import ApiClient from "../../services/ApiClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";
import HeadHunterClient from "../../services/HeadHunterClient";
import VacancyDTO from "../../dto/VacancyDTO";

class Vacancies extends React.Component {

    vacancies: VacancyDTO[] = [];
    vacancyChosenId = null;

    constructor() {
        super();

        this.state = {
            vacanciesLoaded: false,
            vacancyChosen: false,
            desiredPosition: ''
        };

        ApiClient.getCurrentUser().then(res => {
            if (res.ok) {
                let desiredPosition = ''
                res.json().then(json => {
                    desiredPosition = json.desiredPosition !== undefined && json.desiredPosition !== null ? json.desiredPosition : '';
                    this.desiredPosition = desiredPosition
                    HeadHunterClient.getVacancies(desiredPosition).then(res => {
                        if (res.ok) {
                            res.json().then(json => {
                                let items = json['items']

                                for (let index = 0; index < items.length; index++) {
                                    let newVacancy = new VacancyDTO()
                                    newVacancy.id = items[index].id;
                                    newVacancy.title = items[index].name;

                                    let employer = items[index].employer
                                    let logo_urls = employer["logo_urls"]
                                    if (logo_urls !== null) {
                                        newVacancy.imgUrl = logo_urls["240"]
                                    } else {
                                        newVacancy.imgUrl = 'https://www.pngall.com/wp-content/uploads/8/Job-Work-PNG-File.png'
                                    }
                                    newVacancy.employer = employer["name"]
                                    let area = items[index].area
                                    newVacancy.city = area["name"]
                                    newVacancy.url = items[index].alternate_url
                                    let experience = items[index].experience
                                    newVacancy.workExperience = experience["name"]

                                    this.vacancies.push(newVacancy)
                                }

                                this.setState({vacanciesLoaded: true})
                            })
                        } else {
                            console.log("Error: could not get vacancies!")
                        }
                    });
                })
            } else {
                console.log("Error: could not get user!")
            }
        });

        this.handleSubmitResult = this.handleSubmitResult.bind(this);
    }

    handleSubmitResult(event) {
        this.setState({vacancyChosen: true})
        this.vacancyChosenId = event.currentTarget.getAttribute("data-value")
    }

    render() {
        if (this.state.vacancyChosen) {
            return (<Navigate to={'/vacancy/' + this.vacancyChosenId}/>);
        }

        let vacanciesRender = []
        for (let i = 0; i < this.vacancies.length; i++) {
            vacanciesRender.push(
                <div className="col-1-of-3">
                    <div className="card">
                        <div className="card__side card__side--front">
                            <div className="card__title">
                                <img loading="lazy" alt="" src={this.vacancies[i].imgUrl} width="240" height="135"/>
                            </div>
                            <div className="card__details">
                                <ul>
                                    <li>{this.vacancies[i].title}</li>
                                    <li>Компания: {this.vacancies[i].employer}</li>
                                    <li>Город: {this.vacancies[i].city}</li>
                                    <li>Опыт работы: {this.vacancies[i].workExperience}</li>
                                </ul>
                            </div>
                        </div>
                        <div className="card__side card__side--back card__side--back">
                            <div className="card__cta">
                                <div className="card__rating-box">
                                    <p className="card__rating-only"></p>
                                </div>
                                <a target="_blank" rel="noopener noreferrer" href={this.vacancies[i].url}
                                   className="btn btn--white">Ссылка на вакансию</a>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        let rowsRender = []
        for (let i = 0; i < this.vacancies.length; i += 3) {
            rowsRender.push(
                <div className="row">
                    {vacanciesRender[i]}
                    {i + 1 <= this.vacancies.length - 1 ? vacanciesRender[i + 1] : null}
                    {i + 2 <= this.vacancies.length - 1 ? vacanciesRender[i + 2] : null}
                </div>
            );
        }

        return (
            <div>
                <ApplicationHeader/>
                <div>
                    <h3 type="courses_page_title">{this.desiredPosition === '' ? "Вакансии" : "Вакансии для " + this.desiredPosition}</h3>
                    {this.state.vacanciesLoaded ? rowsRender : <Loader/>}
                </div>
            </div>
        )
    }
}

function Loader() {
    return (
        <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "65vh"}}>
            <HashLoader size={180} color={"#00aad5"}/>
        </div>
    )
}

export default Vacancies;
