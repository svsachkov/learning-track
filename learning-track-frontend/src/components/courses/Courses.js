import * as React from "react";
import {Navigate} from "react-router-dom";
import HashLoader from "react-spinners/HashLoader";

import './Courses.css';

import ApiClient from "../../services/ApiClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";
import CourseDTO from "../../dto/CourseDTO";

class Courses extends React.Component {

    courses: CourseDTO[] = [];
    courseChosenId = null;

    constructor() {
        super();

        this.state = {coursesLoaded: false, courseChosen: false};

        ApiClient.getCourses(localStorage.getItem('search')).then(res => {
            if (res.ok) {
                res.json().then(json => {
                    for (let index = 0; index < json['content'].length; index++) {
                        let content = json['content']

                        let newCourse = new CourseDTO()
                        newCourse.id = content[index].id;
                        newCourse.title = content[index].title;
                        newCourse.description = content[index].description;
                        newCourse.externalLink = content[index].externalLink;
                        newCourse.imageUrl = content[index].imageUrl;
                        newCourse.category = content[index].category;
                        newCourse.price = content[index].price;
                        newCourse.headline = content[index].headline;
                        newCourse.rating = Number(content[index].rating).toFixed(2);
                        newCourse.completed = content[index].completed;
                        newCourse.liked = content[index].liked;

                        this.courses.push(newCourse)
                    }

                    this.setState({coursesLoaded: true})
                })
            } else {
                console.log("Error: could not get courses!")
            }
        });

        localStorage.setItem('search', '')

        this.handleSubmitResult = this.handleSubmitResult.bind(this);
    }

    handleSubmitResult(event) {
        this.setState({courseChosen: true})
        this.courseChosenId = event.currentTarget.getAttribute("data-value")
    }

    render() {
        if (this.state.courseChosen) {
            return (<Navigate to={'/course/' + this.courseChosenId}/>);
        }

        let coursesRender = []
        for (let i = 0; i < this.courses.length; i++) {
            coursesRender.push(
                <div className="col-1-of-3">
                    <div className="card">
                        <div className="card__side card__side--front">
                            <div className="card__title">
                                <img loading="lazy" alt="" src={this.courses[i].imageUrl}/>
                            </div>
                            <div className="card__details">
                                <ul>
                                    <li>{this.courses[i].title}</li>
                                    <li>Категория: {this.courses[i].category}</li>
                                    <li>Стоимость: {this.courses[i].price}</li>
                                    <li>{this.courses[i].headline}</li>
                                </ul>
                            </div>
                        </div>
                        <div className="card__side card__side--back card__side--back">
                            <div className="card__cta">
                                <div className="card__rating-box">
                                    <p className="card__rating-only">Средняя оценка:</p>
                                    <p className="card__rating-value">{this.courses[i].rating} / 5</p>
                                </div>
                                <a href="#" className="btn btn--white" data-value={this.courses[i].id}
                                   onClick={this.handleSubmitResult}>Подробнее</a>
                                <a target="_blank" rel="noopener noreferrer" href={this.courses[i].externalLink}
                                   className="btn btn--white">Ссылка на курс</a>
                            </div>
                        </div>
                    </div>
                </div>
            );
        }

        let rowsRender = []
        for (let i = 0; i < this.courses.length; i += 3) {
            rowsRender.push(
                <div className="row">
                    {coursesRender[i]}
                    {i + 1 <= this.courses.length - 1 ? coursesRender[i + 1] : null}
                    {i + 2 <= this.courses.length - 1 ? coursesRender[i + 2] : null}
                </div>
            );
        }

        return (
            <div>
                <ApplicationHeader/>
                <div>
                    <h3 type="courses_page_title">Курсы</h3>
                    {this.state.coursesLoaded ? rowsRender : <Loader/>}
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

export default Courses;
