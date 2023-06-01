import * as React from "react";
import {Navigate} from "react-router-dom";
import HashLoader from "react-spinners/HashLoader";
import CreatableSelect from "react-select/creatable";

import '@fortawesome/fontawesome-free/css/all.css';

import './Profile.css';

import ApiClient from "../../services/ApiClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";
import AuthClient from "../../services/AuthClient";
import User from "../../dto/User";
import UserSkillDTO from "../../dto/UserSkillDTO";

class UserProfile extends React.Component {

    constructor() {
        super();

        this.state = {
            username: '', fullName: '', userCity: '', college: '', birthdayYear: '', desiredPosition: '',
            skillsNumber: 0, updating: true
        };

        ApiClient.getCurrentUser().then(res => {
            if (res.ok) {
                res.json().then(json => {
                    this.setState({
                        username: json.username,
                        fullName: json.fullName || '',
                        userCity: json.city || '',
                        college: json.college || '',
                        birthdayYear: json.birthdayYear || '',
                        desiredPosition: json.desiredPosition || '',


                        isProfileSyncedWithServer: false
                    })
                })
            } else {

            }
        });

        ApiClient.getAllSkillNames()
            .then(res => {
                if (res.ok) {
                    res.json().then(json => {
                        for (let i = 0; i < json.length; i++) {
                            this.skillsOptions.push(
                                {label: json[i], value: json[i]}
                            );
                        }
                    })
                } else {
                    console.log("Error")
                }
            });

        ApiClient.requestSkills()
            .then(res => {
                if (res.ok) {
                    res.json().then(json => {
                        this.updateUserSkills(json)
                    })

                } else {
                    console.log("Error")
                }
            });

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmitResult = this.handleSubmitResult.bind(this);

        this.handleChange2 = this.handleChange2.bind(this);
        this.handleAddSkillButton = this.handleAddSkillButton.bind(this);
        this.deleteButtonClick = this.deleteButtonClick.bind(this);
    }

    handleChange(event) {
        this.setState({
            [event.target.name]: event.target.value
        });

        if (this.state.isProfileSyncedWithServer) {
            this.setState({
                isProfileSyncedWithServer: false
            })
        }
    }

    handleSubmitResult(event) {
        event.preventDefault();

        let user = new User()
        user.fullName = this.state.fullName
        user.city = this.state.userCity
        user.college = this.state.college
        user.birthdayYear = this.state.birthdayYear
        user.desiredPosition = this.state.desiredPosition

        ApiClient.putCurrentUser(user)
            .then(res => {
                if (res.ok) {
                    res.json().then(json => {
                        console.log(json)
                    })
                    this.setState({
                        isProfileSyncedWithServer: true
                    })
                } else {
                    console.log("Error")
                }
            });
    }

    handleAddSkillButton() {
        this.userSkills.push({level: this.skillLevelOptions[0].value});
        this.setState({
            skillsNumber: this.state.skillsNumber + 1
        })
    }

    updateUserSkills(json) {
        this.userSkills = []
        console.log(json)
        for (let i = 0; i < json.length; i++) {
            let userSkill = new UserSkillDTO();
            userSkill.id = json[i].id
            userSkill.username = json[i].username
            userSkill.level = json[i].level
            userSkill.skill = json[i].skill
            this.userSkills.push(userSkill)
        }
        this.userSkills.sort((a,b) => (a.id - b.id))
        this.setState({
            skillsNumber: this.userSkills.length,
            updating: false
        })
    }

    updateSkills() {
        this.setState({
            updating: true
        })
        ApiClient.updateSkills(this.userSkills)
            .then(res => {
                if (res.ok) {
                    res.json().then(json => {
                        this.updateUserSkills(json)
                    })
                } else {
                    console.log("Error")
                }
            });
    }

    handleChange2(newValue, i, type) {
        if (newValue === null) {
            return;
        }
        if (type === "skill") {
            this.userSkills[i].skill = newValue.value
        } else {
            this.userSkills[i].level = newValue.target.id.charAt(newValue.target.id.length - 1)
        }

        if (newValue.__isNew__) {
            this.skillsOptions.push({value: newValue.value, label: newValue.label})
        }
        // console.log(this.userSkills)
        this.updateSkills();
    }

    deleteButtonClick(id, index) {
        this.setState({
            updating: true
        })
        console.log(id);
        if (id === undefined || id === null) {
            this.userSkills.splice(index, 1);
            this.setState({
                updating: false
            })
            return;
        }
        ApiClient.deleteSkill(parseInt(id))
            .then(res => {
                if (res.ok) {
                    res.json().then(json => {
                        this.updateUserSkills(json)
                    })
                } else {
                    console.log("Error")
                }
            });
    }

    skillsOptions = []
    skillLevelOptions = [
        {label: "beginner", value: 1.0},
        {label: "intermediate", value: 2.0},
        {label: "expert", value: 3.0}
    ]
    userSkillsView = []
    userSkills: UserSkillDTO[] = []



    render() {
        if (AuthClient.ACCESS_TOKEN == null) {
            return (<Navigate to='/login'/>)
        }

        const container = document.getElementById('container');

        this.userSkillsView = []
        for (let i = 0; i < this.userSkills.length; i++) {
            let ss = 'toggle-' + i
            // console.log(this.userSkills[i].level)
            this.userSkillsView.push(
                (<div className="SkillView">
                    <CreatableSelect className="SelectMenu"
                        // isClearable
                                     onChange={(e) => this.handleChange2(e, i, "skill")}
                                     styles={selectStyles}
                                     placeholder="Select skill"
                                     options={this.skillsOptions}
                                     defaultValue={{label: this.userSkills[i].skill, value: this.userSkills[i].skill}}
                                     value={{label: this.userSkills[i].skill, value: this.userSkills[i].skill}}
                                     key={"skill"+i}
                    />
                    <div className="toggle-row">
                        <div className="toggle-option">
                            {this.userSkills[i].level === 1 ?
                                <input type="radio" id={ss + '1'} name={"toggle-group-" + i} checked
                                       onClick={(e) => this.handleChange2(e, i, "level")}/> :
                                <input type="radio" id={ss + '1'} name={"toggle-group-" + i}
                                       onClick={(e) => this.handleChange2(e, i, "level")}/>
                            }
                            <label htmlFor={ss + '1'}>1</label>
                        </div>
                        <div className="toggle-option">
                            {this.userSkills[i].level === 2 ?
                                <input type="radio" id={ss + '2'} name={"toggle-group-" + i} checked
                                       onClick={(e) => this.handleChange2(e, i, "level")}/> :
                                <input type="radio" id={ss + '2'} name={"toggle-group-" + i}
                                       onClick={(e) => this.handleChange2(e, i, "level")}/>
                            }
                            <label htmlFor={ss + '2'}>2</label>
                        </div>
                        <div className="toggle-option">
                            {this.userSkills[i].level === 3 ?
                                <input type="radio" id={ss + '3'} name={"toggle-group-" + i} checked
                                       onClick={(e) => this.handleChange2(e, i, "level")}/> :
                                <input type="radio" id={ss + '3'} name={"toggle-group-" + i}
                                       onClick={(e) => this.handleChange2(e, i, "level")}/>
                            }
                            <label htmlFor={ss + '3'}>3</label>
                        </div>
                    </div>
                    <span className="fa fa-trash" onClick={() => this.deleteButtonClick(this.userSkills[i].id, i)}></span>
                </div>)
            )
        }

        return (<div>
            <ApplicationHeader/>
            <div className="UserProfile">
                <div className="container" id="container">
                    <div className="form-container sign-up-container">
                        {/*<form action="#">*/}
                        <div className="wow">
                            <h4>Мои навыки</h4>
                            <span>Уровни: 1 - начальный, 2 - средний, 3 - продвинутый</span>
                            {this.userSkillsView}
                            {this.state.updating ? <Loader/> :
                            <button className="ghost" onClick={this.handleAddSkillButton}>Добавить навык</button>}
                        </div>
                    </div>
                    <div className="form-container sign-in-container">
                        <form action="#" onSubmit={this.handleSubmitResult}>
                            <h4>Профиль</h4>
                            <span>{this.state.username}</span>
                            <div>
                                <label type="user_profile_fields_labels">
                                    {this.state.fullName === '' ? "ФИО (пока не заполнена)" : "ФИО"}
                                </label>
                                <input type="user_profile_text"
                                       placeholder="ФИО"
                                       value={this.state.fullName}
                                       name="fullName"
                                       onChange={this.handleChange}/>
                                <label type="user_profile_fields_labels">
                                    {this.state.birthdayYear === '' ? "Год рождения (пока не заполнен)" : "Год рождения"}
                                </label>
                                <input type="user_profile_text"
                                       placeholder="Год рождения"
                                       value={this.state.birthdayYear}
                                       name="birthdayYear"
                                       onChange={this.handleChange}/>
                                <label type="user_profile_fields_labels">
                                    {this.state.userCity === '' ? "Город (пока не заполнена)" : "Город"}
                                </label>
                                <input type="user_profile_text"
                                       placeholder="Город"
                                       value={this.state.userCity}
                                       name="userCity"
                                       onChange={this.handleChange}/>
                                <label type="user_profile_fields_labels">
                                    {this.state.college === '' ? "Учебное заведение (пока не заполнено)" : "Учебное заведение"}
                                </label>
                                <input type="user_profile_text"
                                       placeholder="Учебное заведение"
                                       value={this.state.college}
                                       name="college"
                                       onChange={this.handleChange}/>
                                <label type="user_profile_fields_labels">
                                    {this.state.desiredPosition === '' ? "Желаемая должность (пока не заполнена)" : "Желаемая должность"}
                                </label>
                                <input type="user_profile_text"
                                       placeholder="Желаемая должность"
                                       value={this.state.desiredPosition}
                                       name="desiredPosition"
                                       onChange={this.handleChange}/>
                            </div>
                            <input type="submit"
                                   className="user_profile_update_submit"
                                   value={this.state.isProfileSyncedWithServer ? "Данные успешно обновлены!" : "Обновить данные профиля"}
                                   disabled={this.state.isProfileSyncedWithServer}
                            />
                        </form>
                    </div>
                    <div className="overlay-container">
                        <div className="overlay">
                            <div className="overlay-panel overlay-left">
                                <h1>Мои навыки</h1>
                                <p>Здесь Вы можете редактировать свои навыки</p>
                                <button className="ghost" id="signIn" onClick={() => {container.classList.remove("right-panel-active");}}>Перейти к профилю</button>
                            </div>
                            <div className="overlay-panel overlay-right">
                                <h1>Мой профиль</h1>
                                <p>Здесь Вы можете редактировать данные своего профиля</p>
                                <button className="ghost" id="signUp" onClick={() => {container.classList.add("right-panel-active");}}>Перейти к навыкам</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>)
    }
}

const selectStyles = {
    control: (provided) => ({
        ...provided,
        minHeight: '30px',
        width: '200px',
        height: '40px',
        fontSize: '20pt',
        cursor: 'pointer',
    }),
    option: (provided) => ({
        ...provided,
        minHeight: '30px',
        height: '30px',
        fontSize: '12pt',
        cursor: 'pointer'
    }),
}

function Loader() {
    return (
        <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "65vh"}}>
            <HashLoader size={180} color={"#00aad5"}/>
        </div>
    )
}

export default UserProfile;
