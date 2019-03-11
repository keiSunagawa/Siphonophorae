import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Route, Link, HashRouter } from 'react-router-dom';

import { Container } from 'react-bootstrap';

import { FileViewerApp } from './fileviewer/FileViewerApp'
import { SimqlApp } from './simql/SimqlApp'

const Header = () => (
  <div>
    <p>Header</p>
    <ul>
      <li><Link to="/">Home</Link></li>
      <li><Link to="/fv">FV</Link></li>
      <li><Link to="/simql">SIMQL</Link></li>
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
        <Route path="/simql" component={SimqlApp} />
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
