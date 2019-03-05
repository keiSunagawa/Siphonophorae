import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Route, Link, HashRouter } from 'react-router-dom';

import { Container } from 'react-bootstrap';

import { FileViewerApp } from './fileviewer/FileViewerApp'

const Header = () => (
  <div>
    <p>Header</p>
    <ul>
      <li><Link to="/">Home</Link></li>
      <li><Link to="/fv">FV</Link></li>
    </ul>
  </div>
);

const Home = () => (
  <div>
    <h2>Home</h2>
  </div>
);

const Root = () => (
  <Container>
    <HashRouter>
      <div>
        <Header />
        <Route exact path="/" component={Home} />
        <Route path="/fv" component={FileViewerApp} />
      </div>
    </HashRouter>
  </Container>
);

ReactDOM.render(
  <div>
    <Root />
  </div>,
    document.getElementById('root')
);
