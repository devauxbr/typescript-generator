import {Injectable} from "@angular/core";
import {Headers, Http, RequestOptionsArgs, Response} from "@angular/http";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

@Injectable()
export class AngularHttpClient implements HttpClient {

    constructor(private http: Http) {}

    public request(requestConfig: { method: string; url: string; queryParams?: any; data?: any }): RestResponse<any> {
        const params: RequestOptionsArgs = {
            method: requestConfig.method
        };
        if (requestConfig.method === 'POST') {
            params.headers = new Headers({
                'Content-Type': 'application/json'
            });
            params.body = JSON.stringify(requestConfig.data);
        }
        return this.http.request(requestConfig.url, params)
            .map(res => res.json());
    }
}
