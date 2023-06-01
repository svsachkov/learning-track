import React from 'react';
import ReactDOM from 'react-dom';
import {BrowserRouter, Routes, Route, useParams} from "react-router-dom";

import './index.css';

import Auth from "./components/auth/Auth";
import Course from "./components/courses/Course";
import Courses from "./components/courses/Courses";
import Track from "./components/track/Track";
import UserProfile from "./components/profile/Profile";
import Vacancies from "./components/vacancies/Vacancies";

const CourseWrapper = (props) => {
    const params = useParams();
    return <Course {...{...props, match: {params}}} />
}

ReactDOM.render(<BrowserRouter>
    <Routes>
        <Route path="/" element={<Track/>}/>
        <Route path="auth" element={<Auth/>}/>
        <Route path="login" element={<Auth/>}/>
        <Route path="register" element={<Auth/>}/>
        <Route path="login-or-register" element={<Auth/>}/>
        <Route path="profile" element={<UserProfile/>}/>
        <Route path="track" element={<Track/>}/>
        <Route path="courses" element={<Courses/>}/>
        <Route path="vacancies" element={<Vacancies/>}/>
        <Route path="course/:id" element={<CourseWrapper/>}/>
    </Routes>
</BrowserRouter>, document.getElementById('root'));
