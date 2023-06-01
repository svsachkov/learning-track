import * as React from "react";
import {animated as a} from "react-spring"
import HashLoader from "react-spinners/HashLoader";

import './Course.css';

import ApiClient from "../../services/ApiClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";
import CourseDTO from "../../dto/CourseDTO";

class Course extends React.Component {

    course: CourseDTO = new CourseDTO();

    constructor(props) {
        super(props);

        this.state = {
            courseLiked: false,
            courseLoaded: false,
            courseCompleted: false
        };

        let {id} = this.props.match.params;

        ApiClient.getCourse(id).then(res => {
            if (res.ok) {
                res.json().then(json => {
                    this.course.id = json.id;
                    this.course.title = json.title;
                    this.course.description = json.description;
                    this.course.externalLink = json.externalLink;
                    this.course.liked = json.liked;
                    this.course.completed = json.completed;

                    this.setState({
                        courseLoaded: true,
                        courseCompleted: this.course.completed,
                        courseLiked: this.course.liked
                    })
                })
            } else {
                console.log("Error: could not get course by course_id!")
            }
        });

        this.handleLikeButton = this.handleLikeButton.bind(this);
        this.handleUnlikeButton = this.handleUnlikeButton.bind(this);
        this.handleMarkAsReadButton = this.handleMarkAsReadButton.bind(this);
    }

    handleMarkAsReadButton() {
        ApiClient.materialCompleted(this.course.id).then(res => {
            if (res.ok) {
                this.setState({courseCompleted: true})
            } else {
                console.log("Error: could not mark course as completed!")
            }
        });
    }

    handleLikeButton() {
        ApiClient.learningMaterialLike(this.course.id, "course").then(res => {
            if (res.ok) {
                this.setState({courseLiked: true})
            } else {
                console.log("Error: could not like the material!")
            }
        });
    }

    handleUnlikeButton() {
        ApiClient.learningMaterialUnlike(this.course.id, "course").then(res => {
            if (res.ok) {
                this.setState({courseLiked: false})
            } else {
                console.log("Error: could not remove like from the material!")
            }
        });
    }

    render() {
        if (!this.state.courseLoaded) {
            return (
                <div>
                    <ApplicationHeader/>
                    <div className="course">
                        <Loader/>
                    </div>
                </div>)
        }

        return (
            <div>
                <ApplicationHeader/>
                <div className="course">
                    <h1 className="course_title">{this.course.title}</h1>
                    <p className="course_description">{this.course.description}</p>
                    {this.course.externalLink !== "" ?
                        (<a rel="noopener noreferrer" href={this.course.externalLink} target="_blank">
                            <button className="ExternalLinkButton">Ссылка на курс</button>
                        </a>) : null}
                    <div className="button_row">
                        {this.state.courseCompleted ?
                            (<button className="MarkAsCompleted after" disabled={true} style={{pointerEvents: "none"}}>
                                Курс пройден!
                            </button>) :
                            (<button className="MarkAsCompleted" onClick={this.handleMarkAsReadButton}>
                                Отметить пройденным
                            </button>)}
                        {this.state.courseLiked ?
                            (<button type="course_liked" onClick={this.handleUnlikeButton}>
                                Вы оценили этот курс! (убрать лайк)
                            </button>) :
                            (<button type="course_unliked" onClick={this.handleLikeButton}>
                                Поставить нравится курсу
                            </button>)}
                    </div>
                </div>
            </div>)
    }
}

function Loader() {
    return (
        <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "40vh"}}>
            <HashLoader size={180} color={"#00aad5"}/>
        </div>
    )
}

export default Course;
