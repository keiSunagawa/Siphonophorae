import { exec } from 'child_process'

import * as React from 'react';
import Highlight from 'react-highlight'
import AceEditor from 'react-ace';
import * as simqlMode from './syntax/mode.js'
import 'brace/theme/idle_fingers';
import '../../node_modules/highlight.js/styles/agate.css';
import * as R from 'ramda'

import { MySQLAccessor } from '../modules/MySQLAccessor'
import { SIMQL } from '../scala/frontend-backend-fastopt'
import { Button, Form } from 'react-bootstrap';

import { ItemTable } from '../components/table'

interface SimqlState {
  simqlQuery: string
  error: String
  sql: String
  header: String[]
  body: String[][]
}
export class SimqlApp extends React.Component<{}, SimqlState> {
  appHandler: any
  formatter: SQLFormatter = new SQLFormatter()
  mysql: MySQLAccessor = new MySQLAccessor('./simql-connection.yml')

  constructor(props: {}) {
    super(props)
    // initialize state
    this.state = {
      simqlQuery: "",
      error: "",
      sql: "",
      header: [],
      body: []
    }

    this.appHandler = SIMQL.compile(
      // presenter functions
      () => this.state.simqlQuery,
      (sql: String) => {
        this.formatter.format(
          sql,
          (formatted) => this.setState({ sql: formatted }),
          () => this.setState({ sql }) // format失敗の場合は未フォーマット値を入れる
        )
      },
      (error: String) => { console.log(error) },
      (sql: String) => {
        this.mysql.send(sql).then(({ header, body }) => {
          this.setState({ header, body })
        })
      }
    )

    this.handleClick = this.handleClick.bind(this)
    this.handleQureyForm = this.handleQureyForm.bind(this)

    this.mysql.schemaInfo().then((v) => {
      simqlMode.aceCompletionHelper.items =  R.flatten(v.map((a) => a.colmns))
    })
  }

  handleClick() {
    this.appHandler.submit()
  }

  handleQureyForm(value: string) {
    this.setState({ simqlQuery: value })
  }

  render() {
    return (
      <div className="sipp-contemt">
        <h2>SIMQL</h2>
        <Form>
          <AceEditor
            mode="simql"
            theme="idle_fingers"
            height="100px"
            width="800px"
            enableBasicAutocompletion={true}
            enableLiveAutocompletion={true}
            value={this.state.simqlQuery}
            onChange={this.handleQureyForm}
            name="queryInput"
            editorProps={{$blockScrolling: true}}
          />
        </Form>
        <div className="sipp-contemt">
          <Button
            variant="primary"
            onClick={this.handleClick}
          >Submit</Button>
        </div>
        <SQL code={this.state.sql} />
        <ItemTable
          header={this.state.header}
          body={this.state.body}
        />
      </div>
    )
  }
}

interface SQLProps {
  code: String
}
const SQL = (props: SQLProps) => (
  <div>
    <Highlight className='sql'>{props.code}</Highlight>
  </div>
)

class SQLFormatter {
  format(
    sql: String,
    successCallback: (formatted: String) => void,
    failCallback: () => void
  ) {
    const command = `echo "${sql}" | sqlformat -r -`
    exec(command, (error, stdout,) => {
      if (error) {
        console.error(`[ERROR] ${error}`);
        failCallback()
      } else {
        successCallback(stdout)
      }
    })
  }
}

