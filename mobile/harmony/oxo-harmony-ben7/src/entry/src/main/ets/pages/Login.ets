import { router } from '@kit.ArkUI';
import { DistributedSessionStore } from '../common/DistributedSessionStore';

@Entry
@Component
struct Login {
  @State username: string = '';
  @State password: string = '';
  @State errorMessage: string = '';

  aboutToAppear(): void {
    const params = router.getParams() as Record<string, Object>;
    // Any continuation fields present in navigation params are accepted as the
    // current session state before the login form is shown.
    DistributedSessionStore.restoreFromRouteParams(params);

    const state = DistributedSessionStore.getState();
    if (state.authenticated) {
      router.replaceUrl({
        url: 'pages/Notes'
      });
    }
  }

  private validateCredentials(): boolean {
    const mockUsername = 'admin';
    const mockPassword = '123456';
    return this.username.trim() === mockUsername && this.password.trim() === mockPassword;
  }

  build() {
    Column({ space: 16 }) {
      Text('Notes App')
        .fontSize(30)
        .fontWeight(FontWeight.Bold)

      Text('Login')
        .fontSize(20)
        .fontColor('#4B5563')

      TextInput({ placeholder: 'Username', text: this.username })
        .height(48)
        .onChange((value: string) => {
          this.username = value;
          this.errorMessage = '';
        })

      TextInput({ placeholder: 'Password', text: this.password })
        .height(48)
        .type(InputType.Password)
        .onChange((value: string) => {
          this.password = value;
          this.errorMessage = '';
        })

      Button('Login')
        .width('100%')
        .height(48)
        .onClick(() => {
          if (this.username.trim().length === 0 || this.password.trim().length === 0) {
            this.errorMessage = 'Please enter username and password.';
            return;
          }

          if (!this.validateCredentials()) {
            this.errorMessage = 'Invalid username or password.';
            return;
          }

          DistributedSessionStore.recordLocalLogin(this.username.trim());
          router.pushUrl({
            url: 'pages/Notes'
          });
        })

      if (this.errorMessage.length > 0) {
        Text(this.errorMessage)
          .fontSize(14)
          .fontColor('#DC2626')
      }

      Text('Distributed session restore is enabled for this build.')
        .fontSize(12)
        .fontColor('#6B7280')
    }
    .width('100%')
    .height('100%')
    .justifyContent(FlexAlign.Center)
    .padding({ left: 24, right: 24 })
    .backgroundColor('#F6F8FF')
  }
}
