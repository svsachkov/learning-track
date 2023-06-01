import * as React from "react";
import {Navigate, useNavigate} from "react-router-dom";
import HashLoader from "react-spinners/HashLoader";

import '@fortawesome/fontawesome-free/css/all.css';

import './Track.css';

import ApiClient from "../../services/ApiClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";
import AuthClient from "../../services/AuthClient";
import {TrackDTO} from "../../dto/TrackDTO";
import {TrackStepDTO} from "../../dto/TrackStepDTO";

class Track extends React.Component {

    track: TrackDTO = new TrackDTO();
    tracks: TrackDTO[] = [];

    constructor(props) {
        super(props);

        this.state = {
            trackLoaded: false,
            trackGenerating: false,
            errorMessage: ""
        };

        this.requestTrack();

        this.handleGenerateTrackButton = this.handleGenerateTrackButton.bind(this);
    }

    handleGenerateTrackButton() {
        this.setState({trackGenerating: true});

        ApiClient.generateNewTrack().then(res => {
            if (res.status === 200) {
                this.requestTrack();
                window.location.reload();
            } else if (res.status === 204) {
                this.setState({errorMessage: "Unable to generate a track. Try change your skills or desired position"})
                this.requestTrack();
                setTimeout(() => this.setState({errorMessage: ""}), 10000);
            } else {
                this.setState({errorMessage: "Error occurred"})
                this.requestTrack();
                setTimeout(() => this.setState({errorMessage: ""}), 10000);
                console.log("Error")
            }
        });
    }

    getMaterialType(type) {
        switch (type) {
            case "job":
                return "Вакансия"
            case "article":
                return "Статья"
            case "course":
                return "Курс"
            default:
                return type
        }
    }

    requestTrack() {
        ApiClient.getTracks().then(res => {
            if (res.ok) {
                res.json().then(json => {
                    for (let i = 0; i < json.length; i++) {
                        this.track = new TrackDTO();
                        this.track.trackId = json[i].trackId;
                        this.track.destination = json[i].destination;

                        if (json[i].trackSteps != null) {
                            for (let index = 0; index < json[i].trackSteps.length; index++) {
                                let trackStep: TrackStepDTO = new TrackStepDTO();
                                trackStep.completed = json[i].trackSteps[index].completed;
                                trackStep.stepOrderNumber = json[i].trackSteps[index].stepOrderNumber;
                                trackStep.trackStepId = json[i].trackSteps[index].trackStepId;

                                trackStep.material.id = json[i].trackSteps[index].material.id;
                                trackStep.material.learningMaterialType = json[i].trackSteps[index].material.learningMaterialType
                                trackStep.material.learningMaterialTypeDisplay = this.getMaterialType(json[i].trackSteps[index].material.learningMaterialType);
                                trackStep.material.description = json[i].trackSteps[index].material.description;
                                trackStep.material.title = json[i].trackSteps[index].material.title;

                                this.track.addTrackStep(trackStep);
                            }

                            this.track.trackSteps.sort((a, b) =>
                                a.stepOrderNumber - b.stepOrderNumber);
                        }

                        this.tracks.push(this.track);
                        this.setState({
                            trackLoaded: true
                        })
                    }
                })
            } else {
                console.log("Error")
            }
        });
        this.setState({
            trackGenerating: false
        });
    }

    render() {
        if (AuthClient.ACCESS_TOKEN == null) {
            return (<Navigate to='/login'/>)
        }

        return (
            <div>
                <ApplicationHeader/>
                <div className="Track">
                    {(this.state.trackGenerating) ?
                        <button className="GenerateTrackButton GenerateTrackBeingGeneratedButton" disabled={true}>
                            Генерация трека...
                        </button>
                        :
                        <button className="GenerateTrackButton" onClick={this.handleGenerateTrackButton}>
                            Сгенерировать новый трек
                        </button>
                    }
                    {this.state.trackLoaded && !this.state.trackGenerating ? (
                            <div className="stepa">
                                <ComponentTrackView track={this.track} tracks={this.tracks}/>
                            </div>) : /*!this.state.trackLoaded ? (<Loader/>) :*/
                        this.state.trackGenerating ? (<Loader/>) : null
                    }
                </div>
            </div>
        )
    }
}

function ComponentTrackView(props) {

    function handleDeleteTrack(trackId) {
        console.log(trackId)
        ApiClient.deleteTrackById(trackId).then(res => {
            if (res.ok) {
                window.location.reload()
            } else {
                console.log("Error: could not delete track by track_id!")
            }
        });
    }

    let navigate = useNavigate();

    let aRender = []
    for (let i = 0; i < props.tracks.length; i++) {
        let trackRender = []
        for (let index = 0; index < props.tracks[i].trackSteps.length; index++) {
            trackRender.push((
                <li className="step-wizard-item current-item" onClick={() => {
                    navigate('/' + props.tracks[i].trackSteps[index].material.learningMaterialType + '/' + props.tracks[i].trackSteps[index].material.id);
                }}>
                    <span className="progress-count">{index + 1}</span>
                    {props.tracks[i].trackSteps[index].completed ?
                        <span
                            className="progress-label_completed">{props.tracks[i].trackSteps[index].material.title}</span> :
                        <span className="progress-label">{props.tracks[i].trackSteps[index].material.title}</span>}
                </li>
            ))
        }

        aRender.push(
            <div className="myDiv">
                <h1 className="stepaTitle">
                    {props.tracks[i].destination}
                    <span className="fa fa-trash" onClick={() => handleDeleteTrack(props.tracks[i].trackId)}></span>
                </h1>
                <section className="step-wizard">
                    <ul className="step-wizard-list">
                        {trackRender}
                    </ul>
                </section>
            </div>
        );
    }

    return aRender;
}

function Loader() {
    return (
        <div style={{display: "flex", justifyContent: "center", alignItems: "center", height: "65vh"}}>
            <HashLoader size={180} color={"#00aad5"}/>
        </div>
    )
}

export default Track;