import { formBindingData, FormExtensionAbility } from '@kit.FormKit';
import { Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;

class FormData {
  username: string = '';
  notes: string = '';
}

export default class EntryFormAbility extends FormExtensionAbility {

  onAddForm(want: Want): formBindingData.FormBindingData {
    hilog.info(DOMAIN, 'testTag', 'onAddForm called');

    const data = new FormData();
    data.username = AppStorage.get<string>('username') ?? '';
    data.notes = JSON.stringify(AppStorage.get<string[]>('notes') ?? []);

    return formBindingData.createFormBindingData(data);
  }

  onUpdateForm(formId: string): void {
    hilog.info(DOMAIN, 'testTag', 'onUpdateForm, formId: %{public}s', formId);

    const data = new FormData();
    data.username = AppStorage.get<string>('username') ?? '';
    data.notes = JSON.stringify(AppStorage.get<string[]>('notes') ?? []);

    import('@kit.FormKit').then((formKit) => {
        formKit.formProvider.updateForm(formId, formBindingData.createFormBindingData(data))
        .catch((err: object) => {
            hilog.error(DOMAIN, 'testTag', 'updateForm failed: %{public}s', JSON.stringify(err));
        });
    });
}

  onFormEvent(formId: string, message: string): void {
    hilog.info(DOMAIN, 'testTag', 'onFormEvent, message: %{public}s', message);

    try {
      const event = JSON.parse(message) as Record<string, string>;
      if (event['action'] === 'clearNotes') {
        AppStorage.set<string[]>('notes', []);
        hilog.info(DOMAIN, 'testTag', 'Notes cleared via card event');
      }
    } catch (e) {
      hilog.error(DOMAIN, 'testTag', 'onFormEvent parse error: %{public}s', JSON.stringify(e));
    }
  }

  onRemoveForm(formId: string): void {
    hilog.info(DOMAIN, 'testTag', 'onRemoveForm, formId: %{public}s', formId);
  }
}