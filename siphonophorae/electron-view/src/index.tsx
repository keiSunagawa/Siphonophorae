import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Route, Link, HashRouter } from 'react-router-dom';

import { Container, ButtonGroup, Button } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

import { FileViewerApp } from './fileviewer/FileViewerApp'
import { SimqlApp } from './simql/SimqlApp'

const Header = () => (
  <div className="sipp-header">
    <ButtonGroup size="lg">
      <LinkContainer exact to="/">
        <Button>Home</Button>
      </LinkContainer>
      <LinkContainer to="/simql">
        <Button>SIMQL</Button>
      </LinkContainer>
      <LinkContainer to="/fv">
        <Button>FileViewer</Button>
      </LinkContainer>
    </ButtonGroup>
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
