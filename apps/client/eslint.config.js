import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import react from 'eslint-plugin-react'
import tseslint from 'typescript-eslint'
import prettierPlugin from 'eslint-plugin-prettier'
import prettierConfig from 'eslint-config-prettier'

export default [
  // 1. 공통 무시 파일 설정
  { ignores: ['dist', 'node_modules', 'routeTree.gen.ts'] },

  // 2. 기본 설정 확장
  js.configs.recommended,
  // tseslint.configs.recommended는 배열이므로 전개 구문(...)으로 풀어줍니다.
  ...tseslint.configs.recommended,

  // 3. 메인 설정 객체
  {
    files: ['**/*.{ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
    },
    // 플러그인 등록
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      react,
      prettier: prettierPlugin,
    },
    // 규칙 설정
    rules: {
      // React Hooks 추천 규칙
      ...reactHooks.configs.recommended.rules,
      
      // React 추천 규칙
      ...react.configs.recommended.rules,
      ...react.configs['jsx-runtime'].rules,

      // Prettier 규칙 위반을 에러로 표시
      'prettier/prettier': 'error',

      // React 17+ import React 불필요
      'react/react-in-jsx-scope': 'off',

      // shadcn/ui 스타일 상수 export 허용
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
  },

  // 4. Prettier 설정 (맨 마지막)
  prettierConfig,
]