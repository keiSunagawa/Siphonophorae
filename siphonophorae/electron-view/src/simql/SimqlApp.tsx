import { exec } from 'child_process'
import * as React from 'react';
import Highlight from 'react-highlight'

import { SIMQL } from '../scala/frontend-backend-fastopt'
import { TextAreaInput } from '../components/input'
import { Button, Form } from 'react-bootstrap';
import "../../node_modules/highlight.js/styles/agate.css";

interface SimqlState {
  simqlQuery: String
  error: String
  sql: String
}
export class SimqlApp extends React.Component<{}, SimqlState> {
  appHandler: any
  formatter: SQLFormatter = new SQLFormatter()

  constructor(props: {}) {
    super(props)
    // initialize state
    this.state = {
      simqlQuery: "",
      error: "",
      sql: ""
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
      (sql: String) => { console.log("send to..." + sql) }
    )

    this.handleClick = this.handleClick.bind(this)
    this.handleQureyForm = this.handleQureyForm.bind(this)
  }

  handleClick() {
    this.appHandler.submit()
  }

  handleQureyForm(value: string) {
    this.setState({ simqlQuery: value })
  }

  render() {
    return (
      <div>
        <h2>SIMQL</h2>
        <Form>
          <TextAreaInput
            id="queryInput"
            label="Query"
            handler={this.handleQureyForm} />
        </Form>
        <Button
          variant="primary"
          onClick={this.handleClick}
        >Submit</Button>
        <SQL code={this.state.sql} />
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
