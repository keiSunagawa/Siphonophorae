//import {computedFrom} from 'aurelia-framework';
import { Hello, Service } from './scala-js-test-fastopt'

export class Welcome {
  heading: string = 'Welcome to the Aurelia Navigation App';
  firstName: string = 'John';
  lastName: string = 'Doe';
    previousValue: string = this.fullName;

    pf(s: String) {
        console.log(s)
    }
    constructor() {
        let h = new Hello()
        console.log(h.getA);
        console.log(h.succ(5));
        console.log(h.printman(this.pf));
        // 無名関数もー
        let f = (s: String) => console.log(s)
        h.printman(f)

        // let compiler = Service.makeCompiler(
        //     () => "hoge",
        //     (msg: String) => console.log(msg),
        //     (msg: String) => console.log(msg)
        // )
        // Service.program("fuga").foldMap(compiler)
        let svc = new Service()
        svc.production(
            "fuga ",
            () => "hoge",
            (msg: String) => console.log(msg),
            (msg: String) => console.log(msg)
        )
    }

  //Getters can't be directly observed, so they must be dirty checked.
  //However, if you tell Aurelia the dependencies, it no longer needs to dirty check the property.
  //To optimize by declaring the properties that this getter is computed from, uncomment the line below
  //as well as the corresponding import above.
  //@computedFrom('firstName', 'lastName')
  get fullName(): string {
    return `${this.firstName} ${this.lastName}`;
  }

  submit() {
    this.previousValue = this.fullName;
    alert(`Welcome, ${this.fullName}!`);
  }

  canDeactivate(): boolean | undefined {
    if (this.fullName !== this.previousValue) {
      return confirm('Are you sure you want to leave?');
    }
  }
}

export class UpperValueConverter {
  toView(value: string): string {
    return value && value.toUpperCase();
  }
}
