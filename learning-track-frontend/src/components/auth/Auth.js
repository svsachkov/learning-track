import * as React from "react";
import {Navigate} from "react-router-dom";

import './Auth.css';

import AuthClient from "../../services/AuthClient";
import ApplicationHeader from "../applicationheader/ApplicationHeader";

class Auth extends React.Component {

    constructor() {
        super();

        this.state = {
            username: '', password: '',
            codeSent: false, secondsLeft: 120,
            message: null, authorized: false
        }
    }

    componentDidMount() {
        this.timer = setInterval(() => this.tick(), 1000)
    }

    componentWillUnmount() {
        clearInterval(this.timer);
    }

    tick() {
        if (this.state.codeSent) {
            if (this.state.secondsLeft <= 0) {
                this.setState({
                    codeSent: false,
                    secondsLeft: 120,
                    message: 'Время действия кода истекло. Попробуйте ещё раз.'
                })
            } else {
                this.setState({
                    secondsLeft: this.state.secondsLeft - 1
                })
            }
        }
    }

    render() {
        if (this.state.authorized || AuthClient.ACCESS_TOKEN !== null) {
            return (<Navigate to='/track'/>)
        }

        return (
            <div>
                <ApplicationHeader/>
                {this.state.codeSent === null || !this.state.codeSent ?
                    <Email this={this}/> :
                    <Code this={this}/>}
            </div>);
    }
}

function Email(props) {

    function handleChange(event) {
        props.this.setState({
            [event.target.name]: event.target.value,
            message: ''
        });
    }

    function handleSubmitResult(event) {
        event.preventDefault();

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(props.this.state.username)) {
            props.this.setState({message: 'Неверный формат!'})
        } else {
            props.this.setState({codeSent: true})

            AuthClient.auth(props.this.state.username, '')
                .then((res => {
                    if (res.ok) {
                        AuthClient.USERNAME = props.this.state.username;
                        localStorage.setItem('username', JSON.stringify(props.this.state.username));
                    } else {
                        res.json().then(() => {
                            props.this.setState({message: 'Указана неверная почта!', codeSent: false})
                        }).catch(() => {
                            props.this.setState({message: 'Указана неверная почта!', codeSent: false})
                        })
                    }
                }));
        }
    }

    return (
        <div className="Auth">
            <form onSubmit={handleSubmitResult} style={{alignItems: "center", justifyContent: "center"}}>
                <div className="Auth">
                    <h1>Войдите или зарегистрируйтесь</h1>
                    <p>Чтобы начать пользоваться сервисом Learning Track</p>
                    <div className="email">
                        <input
                            type="text"
                            maxLength="320"
                            placeholder="Электронная почта"
                            value={props.this.state.username}
                            name="username"
                            onChange={handleChange}
                        />
                    </div>
                    <div className="errorMessage">{props.this.state.message}</div>
                    <div className="submitButtonHolder">
                        <input type="submit" className="auth_submit" value="Продолжить"/>
                    </div>
                </div>
            </form>
        </div>
    );
}

function Code(props) {

    function handleSubmitResult(event) {
        event.preventDefault();

        const inputs = Array.from(event.target.parentElement.getElementsByTagName('input'));

        let password = '';
        for (let i = 0; i < inputs.length - 1; i++) {
            password = password + String(inputs[i].value)
        }

        props.this.setState({password: password})

        AuthClient.auth(props.this.state.username, password)
            .then((res => {
                if (res.ok) {
                    AuthClient.USERNAME = props.this.state.username;
                    localStorage.setItem('username', JSON.stringify(props.this.state.username));

                    AuthClient.ACCESS_TOKEN = res.headers.get("Authorization");
                    localStorage.setItem('sessionId', JSON.stringify(res.headers.get("Authorization")));

                    props.this.setState({
                        username: '',
                        password: '',
                        codeSent: false,
                        secondsLeft: 120,
                        message: '',
                        authorized: true
                    });
                } else {
                    res.json().then(() => {
                        props.this.setState({message: 'Неверный код!', password: ''});
                    }).catch(() => {
                        props.this.setState({message: 'Ошибка авторизации!'})
                    })
                }
            }));
    }

    function onInput(event) {
        if (!/^\d*$/.test(event.target.value)) {
            event.target.value = '';
        }

        const inputs = Array.from(event.target.parentElement.getElementsByTagName('input'));
        const index = inputs.indexOf(event.target);

        if (event.target.value >= 0 && index < 4) {
            inputs[index + 1].focus();
        }
    }

    function onKeyDown(event) {
        const inputs = Array.from(event.target.parentElement.getElementsByTagName('input'));
        const index = inputs.indexOf(event.target);

        if (event.key === 'Backspace' && index >= 0) {
            event.preventDefault();

            props.this.setState({
                message: ''
            });

            event.target.value = '';

            if (index > 0) {
                inputs[index - 1].focus();
            }
        }

        if (event.key === 'Delete' && index < 4) {
            event.preventDefault();

            props.this.setState({
                message: ''
            });

            inputs[index + 1].focus();
            event.target.value = '';
        }
    }

    return (
        <div className="Auth">
            <form onSubmit={handleSubmitResult} style={{alignItems: "center", justifyContent: "center"}}>
                <div className="Auth">
                    <h1>Введите код из письма</h1>
                    <p>
                        Мы отправили письмо на <b>{props.this.state.username}</b>
                    </p>
                    <div className="code">
                        <input type="text" maxLength="1" placeholder="X" onInput={onInput} onKeyDown={onKeyDown}/>
                        <input type="text" maxLength="1" placeholder="X" onInput={onInput} onKeyDown={onKeyDown}/>
                        <input type="text" maxLength="1" placeholder="X" onInput={onInput} onKeyDown={onKeyDown}/>
                        <input type="text" maxLength="1" placeholder="X" onInput={onInput} onKeyDown={onKeyDown}/>
                        <input type="text" maxLength="1" placeholder="X" onInput={onInput} onKeyDown={onKeyDown}/>
                    </div>
                    <div className="errorMessage">{props.this.state.message}</div>
                    <p>Время действия кода <span id="mySpan">{props.this.state.secondsLeft}</span> сек.</p>
                    <div className="submitButtonHolder">
                        <input type="submit" className="auth_submit" value="Войти"/>
                    </div>
                </div>
            </form>
        </div>
    );
}

export default Auth;
