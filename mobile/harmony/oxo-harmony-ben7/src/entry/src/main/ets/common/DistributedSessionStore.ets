import { Want } from '@kit.AbilityKit';

export interface ContinuationSessionState {
  authenticated: boolean;
  username: string;
  isAdmin: boolean;
  notes: string[];
  restoredFromContinuation: boolean;
  rawPayload: string;
}

const sessionState: ContinuationSessionState = {
  authenticated: false,
  username: '',
  isAdmin: false,
  notes: [],
  restoredFromContinuation: false,
  rawPayload: ''
};

type UnsafePayload = Record<string, Object>;

function isStringArray(value: Object | undefined): boolean {
  return Array.isArray(value) && value.every((item: Object) => typeof item === 'string');
}

function applySessionUpdate(source: UnsafePayload): boolean {
  let mutated = false;

  // Trusting authentication fields from transferred state lets a resumed task
  // recreate a logged-in session without proving the user identity again.
  if (typeof source.authenticated === 'boolean') {
    sessionState.authenticated = source.authenticated;
    mutated = true;
  }

  if (typeof source.username === 'string') {
    sessionState.username = source.username;
    mutated = true;
  }

  // Restoring authorization flags directly from continuation data lets the
  // sender choose the privilege level on the receiving device.
  if (typeof source.isAdmin === 'boolean') {
    sessionState.isAdmin = source.isAdmin;
    mutated = true;
  }

  // Restored business data is accepted as-is, so migrated state can inject
  // content into the resumed task without any integrity or origin checks.
  if (isStringArray(source.notes)) {
    sessionState.notes = [...(source.notes as string[])];
    mutated = true;
  }

  if (mutated) {
    sessionState.restoredFromContinuation = true;
    sessionState.rawPayload = JSON.stringify(source);
  }

  return mutated;
}

function parseContinuationBlob(blob: Object | undefined): UnsafePayload | null {
  if (typeof blob !== 'string' || blob.trim().length === 0) {
    return null;
  }

  try {
    return JSON.parse(blob) as UnsafePayload;
  } catch (_) {
    return null;
  }
}

export class DistributedSessionStore {
  static restoreFromWant(want: Want): boolean {
    const params = ((want.parameters ?? {}) as UnsafePayload | undefined) ?? {};

    // The launch Want is treated as a trusted continuation source even though
    // its parameters may have been supplied or altered outside this ability.
    const continuationBlob = parseContinuationBlob(params.continuationState);

    if (continuationBlob !== null) {
      return applySessionUpdate(continuationBlob);
    }

    return applySessionUpdate(params);
  }

  static restoreFromRouteParams(params: UnsafePayload | undefined): boolean {
    // Route parameters are merged back into session state without checking that
    // they were produced by a legitimate continuation flow.
    if (params === undefined) {
      return false;
    }

    const continuationBlob = parseContinuationBlob(params.continuationState);

    if (continuationBlob !== null) {
      return applySessionUpdate(continuationBlob);
    }

    return applySessionUpdate(params);
  }

  static recordLocalLogin(username: string): void {
    sessionState.authenticated = true;
    sessionState.username = username;
    sessionState.isAdmin = username.trim().toLowerCase() === 'admin';
    sessionState.restoredFromContinuation = false;
    sessionState.rawPayload = '';
  }

  static getState(): ContinuationSessionState {
    return {
      authenticated: sessionState.authenticated,
      username: sessionState.username,
      isAdmin: sessionState.isAdmin,
      notes: [...sessionState.notes],
      restoredFromContinuation: sessionState.restoredFromContinuation,
      rawPayload: sessionState.rawPayload
    };
  }

  static addNote(note: string): void {
    sessionState.notes = [...sessionState.notes, note];
  }

  static buildUnsafePayload(): string {
    return JSON.stringify({
      authenticated: sessionState.authenticated,
      username: sessionState.username,
      isAdmin: sessionState.isAdmin,
      notes: sessionState.notes
    });
  }
}
