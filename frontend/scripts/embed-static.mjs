import { execSync } from 'node:child_process';
import { cpSync, mkdirSync, rmSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import path from 'node:path';

// 프로젝트 루트를 계산해 경로 관련 오류를 방지합니다.
const __filename = fileURLToPath(import.meta.url);
const scriptsDir = path.dirname(__filename);
const projectRoot = path.resolve(scriptsDir, '..');
const distDir = path.join(projectRoot, 'dist');
const backendStaticDir = path.resolve(projectRoot, '../backend/src/main/resources/static');

try {
  // 1) 리액트를 정적 파일로 빌드합니다.
  console.log('리액트 빌드 시작 (vite build)...');
  execSync('npm run build', { stdio: 'inherit' });

  // 2) 기존 static 폴더를 제거하여 이전 빌드 결과물이 남지 않도록 합니다.
  console.log('이전 static 폴더 정리 중...');
  rmSync(backendStaticDir, { recursive: true, force: true });

  // 3) static 폴더를 다시 만들고 dist 내용을 그대로 복사합니다.
  console.log('정적 자산 복사 중...');
  mkdirSync(backendStaticDir, { recursive: true });
  cpSync(distDir, backendStaticDir, { recursive: true });

  console.log('정적 자산 내장 완료: backend/src/main/resources/static');
} catch (error) {
  console.error('정적 자산 내장 중 오류가 발생했습니다:', error);
  process.exit(1);
}

