import {Injectable, NgModule} from "@angular/core";
import {HttpClientModule, HttpClient as AngularClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import 'rxjs/add/operator/map';

@Injectable()
export class AngularHttpClient implements HttpClient {

    constructor(private http: AngularClient) {}

    public request(requestConfig: { method: string; url: string; queryParams?: any; data?: any }): RestResponse<any> {
        const options: any = {};
        if (requestConfig.queryParams) {
            let httpParams = new HttpParams();
            Object.keys(requestConfig.queryParams).forEach(key => {
                const value = requestConfig.queryParams[key];

                if (value instanceof Array) {
                    (value as Array<any>).forEach((arrayValue) => {
                        httpParams = httpParams.append(key, arrayValue);
                    })
                } else {
                    httpParams = httpParams.set(key, value);
                }
            });
            options.params = httpParams;
        }
        options.body = requestConfig.data;
        return this.http.request(requestConfig.method, requestConfig.url, options);
    }
}
