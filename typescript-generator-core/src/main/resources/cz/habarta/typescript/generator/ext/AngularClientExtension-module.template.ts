
@NgModule({
    imports: [HttpClientModule],
    providers: []
})
export class RestClientModule {
    static forRoot() {
        return {
            ngModule: RestClientModule,
            providers: [
                AngularHttpClient,
$$ClientProviders$$
            ]
        };
    }
}
